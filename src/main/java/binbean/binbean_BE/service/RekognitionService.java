package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.FixedValue;
import binbean.binbean_BE.dto.CurrentSeats;
import binbean.binbean_BE.dto.DetectedItem;
import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.Position;
import binbean.binbean_BE.dto.response.FloorPlanResponse;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.imageio.ImageIO;
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

@Service
public class RekognitionService {

    private final RekognitionClient rekognitionClient;

    public RekognitionService(RekognitionClient rekognitionClient) {
        this.rekognitionClient = rekognitionClient;
    }

    public FloorPlanResponse getCurrentOccupiedSeats(MultipartFile file, FloorList floorList, int floorNumber) throws IOException {
        List<DetectedItem> people = getDetectedItems(file);
        List<Position> seatPositions = floorList.seatPosition();
        Set<Position> occupiedSeats = new HashSet<>();

        for (DetectedItem person : people) {
            for (Position pos : person.positions()) {
                // 사람 위치와 좌석 위치 매칭
                matchSeatPosition(pos, seatPositions).ifPresent(occupiedSeats::add);
            }
        }

        // 점유된 좌석 위치 리스트
        List<Position> occupiedPos = occupiedSeats.stream().toList();
        CurrentSeats currOccupiedSeats = CurrentSeats.create(occupiedPos);

        return FloorPlanResponse.create(floorList, floorNumber, currOccupiedSeats);
    }

    // 이미지 상의 사람의 위치 좌표와 도면 좌표 매핑
    public Optional<Position> matchSeatPosition(Position person, List<Position> seatPositions) {
        Position nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (Position seat : seatPositions) {
            // 픽셀 좌표 간 유클리드 거리 차이
            double xdist = person.x() - seat.x();
            double ydist = person.y() - seat.y();
            double dist = Math.sqrt(Math.pow(person.x() - seat.x(), 2) + Math.pow(person.y() - seat.y(), 2));
            if (dist < minDistance) {
                minDistance = dist;
                nearest = seat;
            }
        }

        // 최대 허용 거리 30픽셀 이내에 사람이 있으면 해당 위치 좌석에 앉았다고 판단
        // FIXME : 이미지 비율에 따라 동적으로 변해야 함 (추후 수정)
        if (minDistance <= 30) return Optional.of(nearest);
        else return Optional.empty();
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
}
