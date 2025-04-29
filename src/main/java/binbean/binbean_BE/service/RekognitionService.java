package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.aws.BoundingBoxDto;
import binbean.binbean_BE.dto.aws.DetectedItem;
import binbean.binbean_BE.dto.aws.PersonDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                .maxLabels(30)
                .minConfidence(70F) // 신뢰도 70% 이상만 필터
                .build();

            DetectLabelsResponse response = client.detectLabels(request);
            List<DetectedItem> list = new ArrayList<>();

            for (Label label : response.labels()) {
                DetectedItem item = new DetectedItem();
                item.setKey(label.name());
                item.setConfidence(label.confidence());

                List<Instance> instances = label.instances();
                int count = instances != null ? instances.size() : 0;
                item.setValue(count);

                if (instances != null && !instances.isEmpty()) {
                    List<PersonDto> persons = instances.stream()
                        .map(instance -> {
                            BoundingBox box = instance.boundingBox();
                            return PersonDto.create(box);

                        })
                        .toList();
                    item.setPersonPositions(persons);
                }
                list.add(item);
            }

            return list;
        } catch (RekognitionException e) {
            e.printStackTrace();
            log.error("Rekognition Exception: ", e.getMessage());
        }
        return null;
    }
}
