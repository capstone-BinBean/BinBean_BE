package binbean.binbean_BE.dto;

import lombok.Builder;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;

@Builder
public record Position(double x, double y) {

    public static Position create(double x, double y) {
        return Position.builder()
            .x(x)
            .y(y)
            .build();
    }

    public static Position from(BoundingBox box) {
        double x = (double) (box.left() * box.width());
        double y = (double) (box.top() * box.height());

        return Position.builder()
            .x(x)
            .y(y)
            .build();
    }
}
