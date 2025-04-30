package binbean.binbean_BE.dto.aws;

import com.google.cloud.vision.v1.NormalizedVertex;
import lombok.Builder;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;

@Builder
public record PositionDto(
    Double x,
    Double y
) {
        public static PositionDto create(BoundingBox boundingBox) {
            Double x = (double) (boundingBox.left() * boundingBox.width());
            Double y = (double) (boundingBox.top() * boundingBox.height());

            return PositionDto.builder()
                .x(x)
                .y(y)
                .build();
        }

        public static PositionDto from(NormalizedVertex vertex) {
            return PositionDto.builder()
                .x((double) vertex.getX())
                .y((double) vertex.getY())
                .build();
        }
    }

