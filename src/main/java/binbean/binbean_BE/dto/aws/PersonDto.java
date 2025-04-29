package binbean.binbean_BE.dto.aws;

import lombok.Builder;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;

@Builder
public record PersonDto(
    Double x,
    Double y
) {
        public static PersonDto create(BoundingBox boundingBox) {
            Double x = (double) (boundingBox.left() * boundingBox.width());
            Double y = (double) (boundingBox.top() * boundingBox.height());

            return PersonDto.builder()
                .x(x)
                .y(y)
                .build();
        }
    }

