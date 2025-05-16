package binbean.binbean_BE.service;

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
import java.util.stream.Collectors;
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
    private final FloorPlanRepository floorPlanRepository;

    public RekognitionService(RekognitionClient rekognitionClient, FloorPlanRepository floorPlanRepository) {
        this.rekognitionClient = rekognitionClient;
        this.floorPlanRepository = floorPlanRepository;
    }

    public FloorPlanResponse getDetectedSeats(MultipartFile file, FloorList floorList, int floorNumber) throws IOException {

        List<DetectedItem> people = getDetectedItems(file).stream()
            .filter(item -> item.key().equals("Person"))
            .toList();

        List<Position> seatPositions = floorList.seatPosition();

        Set<Position> occupiedSeats = new HashSet<>();

        for (DetectedItem person : people) {
            for (Position pos : person.positions()) {
                // 사람 위치와 좌석 위치 매칭
                matchSeatPosition(pos, seatPositions).ifPresent(occupiedSeats::add);
            }
        }

        List<Position> occupiedPos = occupiedSeats.stream().toList();
        CurrentSeats currOccupiedSeats = CurrentSeats.create(occupiedPos);

        return FloorPlanResponse.create(floorList, floorNumber, currOccupiedSeats);
    }

    public Optional<Position> matchSeatPosition(Position person, List<Position> seatPositions) {
        Position nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (Position seat : seatPositions) {
            double dist = Math.pow(person.x() - seat.x(), 2) + Math.pow(person.y() - seat.y(), 2);
            if (dist < minDistance) {
                minDistance = dist;
                nearest = seat;
            }
        }

        if (minDistance <= 10) return Optional.of(nearest);
        else return Optional.empty();
    }

    public List<DetectedItem> getDetectedItems(MultipartFile file) throws IOException {
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
            List<DetectedItem> detectedList = new ArrayList<>();

            for (Label label : response.labels()) {
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
