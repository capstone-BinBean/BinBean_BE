package binbean.binbean_BE.dto.aws;

import lombok.Builder;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;

@Builder
public record BoundingBoxDto(
    Float width,
    Float height,
    Float left,
    Float top
) {

    public static BoundingBoxDto create(BoundingBox boundingBox) {
        return BoundingBoxDto.builder()
            .width(boundingBox.width())
            .height(boundingBox.height())
            .left(boundingBox.left())
            .top(boundingBox.top())
            .build();
    }
}
