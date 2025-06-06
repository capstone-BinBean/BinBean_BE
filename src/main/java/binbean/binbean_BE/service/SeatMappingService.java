package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.FixedValue;
import binbean.binbean_BE.dto.CurrentSeats;
import binbean.binbean_BE.dto.DetectedItem;
import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.Position;
import binbean.binbean_BE.dto.response.FloorPlanResponse;
import binbean.binbean_BE.integration.rekognition.RekognitionImageAnalyzer;
import binbean.binbean_BE.integration.gemini.GeminiResponseParser;
import binbean.binbean_BE.util.ImageUtils;
import binbean.binbean_BE.util.SeatPositionMapper;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class SeatMappingService {
    private final GeminiService geminiService;
    private final RekognitionImageAnalyzer imageAnalyzer;
    private final GeminiResponseParser responseParser;
    private final SeatPositionMapper seatPositionMapper;
    private final PromptBuilderService builderService;

    public SeatMappingService(RekognitionImageAnalyzer imageAnalyzer, GeminiService geminiService,
        GeminiResponseParser responseParser, SeatPositionMapper seatPositionMapper,
        PromptBuilderService builderService) {
        this.imageAnalyzer = imageAnalyzer;
        this.geminiService = geminiService;
        this.responseParser = responseParser;
        this.seatPositionMapper = seatPositionMapper;
        this.builderService = builderService;
    }

    public FloorPlanResponse getCurrentOccupiedSeats(MultipartFile file, FloorList floorList, int floorNumber) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        byte[] imageBytes = ImageUtils.resizeImage(originalImage, FixedValue.CCTV_IMAGE_WIDTH);
        // ImageBytes BASE64로 인코딩
        String encodedImageBytes = Base64.getEncoder().encodeToString(imageBytes);
        // Aws Rekognition으로 검출된 사람 객체
        List<DetectedItem> detectedPeople = imageAnalyzer.getDetectedItems(file);
        // 사람 위치 리스트
        List<Position> peoplePositions = detectedPeople.stream()
            .flatMap(person -> person.positions().stream())
            .toList();
        int peopleCount = !detectedPeople.isEmpty() ? detectedPeople.getFirst().value() : 0;

        // 도면 좌석 위치 리스트
        List<Position> seatPositions = floorList.seatPosition();
        // Gemini 프롬프트
        String prompt = builderService.buildPrompt(encodedImageBytes, peopleCount, peoplePositions, seatPositions);
        // 점유된 좌석
        Optional<CurrentSeats> currOccupiedSeats;

        try {
            var geminiResponse = geminiService.askGeminiWithImage(prompt, encodedImageBytes);
            log.info("gemini response: {}", geminiResponse);

            currOccupiedSeats = responseParser.parse(geminiResponse);
        } catch (Exception e) {
            log.warn("Gemini API 호출 시 Exception 발생. Euclidean 방식으로 처리합니다.", e);
            currOccupiedSeats = Optional.empty();
        }

        // Gemini에서 도면과 매핑된 좌석을 못가져왔거나 Exception이 발생했을 경우, 유클리드 거리 기반 계산으로 재처리
        if (currOccupiedSeats.isEmpty()) {
            // Euclidean distance 위치값 셋
            Set<Position> occupiedSeatsByEuclid = new HashSet<>();

            for (DetectedItem person : detectedPeople) {
                for (Position pos : person.positions()) {
                    // 사람 위치와 좌석 위치 매칭(유클리드 거리 기반)
                    seatPositionMapper.matchSeatPosition(pos, seatPositions).ifPresent(occupiedSeatsByEuclid::add);
                }
            }
            // 점유된 좌석 위치 리스트
            currOccupiedSeats = Optional.ofNullable(
                CurrentSeats.create(occupiedSeatsByEuclid.stream().toList()));
        }

        return FloorPlanResponse.create(floorList, floorNumber, currOccupiedSeats.orElse(null));
    }
}
