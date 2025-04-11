package binbean.binbean_BE.dto;

import lombok.Builder;

@Builder
public record Position(double x, double y) {

    public static Position create(double x, double y) {

        return Position.builder()
            .x(x)
            .y(y)
            .build();
    }
}
