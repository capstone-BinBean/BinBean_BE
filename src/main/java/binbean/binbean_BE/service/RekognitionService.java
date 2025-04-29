package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.aws.DetectedItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

@Service
public class RekognitionService {

    private static final Logger log = LoggerFactory.getLogger(RekognitionService.class);
    private final RekognitionClient client;

    public RekognitionService(RekognitionClient client) {
        this.client = client;
    }

    public List<DetectedItem> detectLabels(MultipartFile file) throws IOException {
        try {
            Image awsImage = Image.builder()
                .bytes(SdkBytes.fromByteArray(file.getBytes()))
                .build();

            DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(awsImage)
                .maxLabels(10)
                .minConfidence(70F) // 신뢰도 70% 이상만 필터
                .build();

            DetectLabelsResponse response = client.detectLabels(request);
            List<DetectedItem> list = new ArrayList<>();

            for (Label label : response.labels()) {
                DetectedItem item = new DetectedItem();
                item.setKey(label.name());
                item.setConfidence(label.confidence());
                Integer count = label.instances() != null ? label.instances().size() : 0;
                item.setValue(count);
                list.add(item);
            }

            //
            // 탐지 인스턴스 수가 없는 경우, count = 1 로 최소 보장 (예: "Indoors" 같은 Scene 레이블)
//            labelCountMap.put(label.name(), count > 0 ? count : 1);

            return list;
        } catch (RekognitionException e) {
            e.printStackTrace();
            log.error("Rekognition Exception: ", e.getMessage());
        }
        return null;
    }
}
