package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.FixedValue;
import binbean.binbean_BE.dto.CurrentSeats;
import binbean.binbean_BE.dto.DetectedItem;
import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.Position;
import binbean.binbean_BE.dto.response.FloorPlanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Instance;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

@Slf4j
@Service
public class RekognitionService {
    private final RekognitionClient rekognitionClient;
    private final GeminiService geminiService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public RekognitionService(RekognitionClient rekognitionClient, GeminiService geminiService) {
        this.rekognitionClient = rekognitionClient;
        this.geminiService = geminiService;
    }

    public FloorPlanResponse getCurrentOccupiedSeats(MultipartFile file, FloorList floorList, int floorNumber) throws IOException {
        List<DetectedItem> people = getDetectedItems(file);
        List<Position> seatPositions = floorList.seatPosition();
//        Set<Position> occupiedSeats = new HashSet<>();
        Optional<CurrentSeats> currOccupiedSeats = Optional.empty();

        var peoplePositions = people.stream().map(DetectedItem::positions).toList();

        try {
            String peoplePositionsJson = objectMapper.writeValueAsString(peoplePositions);
            String seatPositionsJson = objectMapper.writeValueAsString(seatPositions);
            // JSON 반환 예시
            String currentSeatsJson = objectMapper.writeValueAsString(CurrentSeats.create(List.of(Position.create(10, 10), Position.create(10, 10))));

            String prompt =
                "다음은 이미지에서 검출된 사람들의 위치입니다:\n" +
                    peoplePositionsJson + "\n\n" +
                    "아래는 도면 상에 정의된 좌석들의 위치입니다:\n" +
                    seatPositionsJson + "\n\n" +
                    "이제 다음 조건에 따라, 사람의 위치를 해당 좌석에 매핑해 주세요:\n" +
                    "1. 사진 이미지의 크기와 비율을 고려해 주세요.\n" +
                    "2. 사진의 왜곡을 평면화 처리 등을 통해 보정한 뒤(Perspective Transform, 투시 변환), 도면 좌석 위치와 정렬해 주세요.\n" +
                    "3. 사진의 회전 정도도 고려해 주세요.\n" +
                    "4. 사람의 위치가 좌석과 충분히 가까운 경우, 해당 좌석에 앉아 있다고 판단해 주세요.\n\n" +
                    "결과는 사람이 앉아 있는 좌석만 포함하여 아래 형식의 JSON으로 반환해 주세요:\n" +
                    currentSeatsJson;

            var response = geminiService.askGeminiWithImage(prompt, Base64.getEncoder().encodeToString(file.getBytes()));
            log.info("gemini response: {}", response);

            currOccupiedSeats = parseJsonToCurrentSeats(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // FIXME : 추후 gemini api와 혼합하여 사용 예정
//        for (DetectedItem person : people) {
//            for (Position pos : person.positions()) {
//                // 사람 위치와 좌석 위치 매칭
//                matchSeatPosition(pos, seatPositions).ifPresent(occupiedSeats::add);
//            }
//        }

        // 점유된 좌석 위치 리스트
//        List<Position> occupiedPos = occupiedSeats.stream().toList();
//        CurrentSeats currOccupiedSeats = CurrentSeats.create(occupiedPos);

        return FloorPlanResponse.create(floorList, floorNumber, currOccupiedSeats.orElse(null));
    }

    // 이미지 상의 사람의 위치 좌표와 도면 좌표 매핑 (감지된 사람을 가장 가까운 좌석에 매핑)
    public Optional<Position> matchSeatPosition(Position person, List<Position> seatPositions) {
        Position nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Position seat : seatPositions) {
            // 픽셀 좌표 간 유클리드 거리 차이
            double xdist = person.x() - seat.x();
            double ydist = person.y() - seat.y();
            double dist = Math.sqrt(Math.pow(person.x() - seat.x(), 2) + Math.pow(person.y() - seat.y(), 2));

            RekognitionService.log.info("거리차: ", xdist, ydist, dist);
            if (dist < minDistance) {
                minDistance = dist;
                nearest = seat;
            }
        }

        // 동적 임계값 계산
        double threshold = getDynamicThreshold(seatPositions);

        // 최대 허용 거리 이내에 사람이 있으면 해당 위치 좌석에 앉았다고 판단
        if (minDistance <= threshold) return Optional.of(nearest);
        else return Optional.empty();
    }

    // 도면 상의 좌석 간 평균 거리 계산
    public double calculateAverageSeatDistance(List<Position> seatPositions) {
        double totalDistance = 0.0;
        int count = 0;
        for (int i=0; i < seatPositions.size(); i++) {
            for (int j=i+1; j < seatPositions.size(); j++) {
                double dx = seatPositions.get(i).x() - seatPositions.get(j).x();
                double dy = seatPositions.get(i).y() - seatPositions.get(j).y();
                double dist = Math.sqrt(dx * dx + dy * dy);

                totalDistance += dist;
                count++;
            }
        }

        return count == 0 ? 0.0 : (totalDistance/count);
    }

    // 평균 좌석 거리 기반 임계값 비율 계산 (사람과 좌석 간 허용 거리)
    public double getDynamicThreshold(List<Position> seatPositions) {
        double avgSeatDistance = calculateAverageSeatDistance(seatPositions);
        return avgSeatDistance * 0.4;
    }

    private List<DetectedItem> getDetectedItems(MultipartFile file) throws IOException {
        try {
            Image awsImage = Image.builder()
                .bytes(SdkBytes.fromByteArray(file.getBytes()))
                .build();

            DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(awsImage)
                .maxLabels(30)
                .minConfidence(70F) // 신뢰도 70% 이상만 필터
                .build();

            DetectLabelsResponse response = rekognitionClient.detectLabels(request);
            // 전체 검출된 객체들 중 "사람"만 필터링
            List<Label> personLabels = response.labels().stream()
                .filter(item -> item.name().equalsIgnoreCase(FixedValue.PERSON))
                .toList();
            List<DetectedItem> detectedList = new ArrayList<>();

            for (Label label : personLabels) {
                List<Instance> instances = label.instances();
                // 각 레이블 별 객체 수
                Integer count = instances != null ? instances.size() : 0;

                if (instances != null && !instances.isEmpty()) {
                    // multipart file로 받은 이미지의 사이즈 구하기
                    BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();

                    List<Position> positions = instances.stream()
                        .map( instance -> {
                            BoundingBox box = instance.boundingBox();
                            return Position.convertToPixel(box, width, height);
                        })
                        .toList();

                    DetectedItem item = DetectedItem.create(
                        label.name(),
                        count,
                        label.confidence(),
                        positions
                    );
                    detectedList.add(item);
                }
            }
            return detectedList;
        } catch (RekognitionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Optional<CurrentSeats> parseJsonToCurrentSeats(String rawJson) {
        try {
            var json = cleanJsonMarkdown(rawJson);
            return Optional.ofNullable(objectMapper.readValue(json, CurrentSeats.class));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String cleanJsonMarkdown(String rawJson) {
        return rawJson
            .replace("```json", "")
            .replace("```", "")
            .trim();
    }
}
