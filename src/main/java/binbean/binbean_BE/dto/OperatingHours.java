package binbean.binbean_BE.dto;

import lombok.Builder;

@Builder
public record OperatingHours(String startTime, String endTime) {

    public static OperatingHours create(String startTime, String endTime) {

        return OperatingHours.builder()
            .startTime(startTime)
            .endTime(endTime)
            .build();
    }
}
