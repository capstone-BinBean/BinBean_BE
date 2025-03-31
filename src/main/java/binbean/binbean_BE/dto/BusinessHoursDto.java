package binbean.binbean_BE.dto;

import lombok.Getter;

@Getter
public class BusinessHoursDto {

    private final String startTime;
    private final String endTime;

    public BusinessHoursDto(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
