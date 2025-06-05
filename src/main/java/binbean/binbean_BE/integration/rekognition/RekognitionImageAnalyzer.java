package binbean.binbean_BE.integration.rekognition;

import binbean.binbean_BE.constants.Constants.FixedValue;
import binbean.binbean_BE.dto.DetectedItem;
import binbean.binbean_BE.dto.Position;
import binbean.binbean_BE.util.ImageUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class RekognitionImageAnalyzer {

    private final RekognitionClient rekognitionClient;

    public RekognitionImageAnalyzer(RekognitionClient rekognitionClient) {
        this.rekognitionClient = rekognitionClient;
    }

    public List<DetectedItem> getDetectedItems(MultipartFile file) throws IOException {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            byte[] imageBytes = ImageUtils.resizeImage(originalImage, FixedValue.CCTV_IMAGE_WIDTH);

            Image awsImage = Image.builder()
                .bytes(SdkBytes.fromByteArray(imageBytes))
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
            throw new RuntimeException(e);
        }
    }
}
