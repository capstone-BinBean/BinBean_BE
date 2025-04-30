package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.aws.DetectedItem;
import binbean.binbean_BE.dto.aws.PositionDto;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.LocalizedObjectAnnotation;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VisionService {

    public VisionService() {}

    public List<DetectedItem> detectObjects(MultipartFile file) throws IOException {

        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(file.getInputStream());

        Image image = Image.newBuilder().setContent(imgBytes).build();
        Feature feature = Feature.newBuilder()
            .setType(Feature.Type.OBJECT_LOCALIZATION)
            .setMaxResults(40)
            .build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
            .setImage(image)
            .addFeatures(feature)
            .build();

        requests.add(request);

        List<DetectedItem> list = new ArrayList<>();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
                    System.out.format("Object name: %s%n", entity.getName());
                    System.out.format("Confidence: %s%n", entity.getScore());
                    System.out.format("Normalized Vertices:%n");
                    entity
                        .getBoundingPoly()
                        .getNormalizedVerticesList()
                        .forEach(vertex -> System.out.format("- (%s, %s)%n", vertex.getX(), vertex.getY()));

                    DetectedItem item = new DetectedItem();
                    item.setKey(entity.getName());
                    item.setConfidence(entity.getScore());
                    // 기본적으로 객체 하나당 1개 인식해서 그냥 1로 고정
                    item.setValue(1);

                    BoundingPoly box = entity.getBoundingPoly();
                    if (box.getNormalizedVerticesCount() > 0) {
                        List<PositionDto> positions = box.getNormalizedVerticesList().stream()
                            .map(PositionDto::from)
                            .toList();
                        item.setPositions(positions);


                    }
                    list.add(item);
                }
            }
        }
        return list;
    }
}
