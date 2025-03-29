package binbean.binbean_BE.dto.marker;

import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import lombok.Builder;

@Builder
public record FloorSeatsDto(
    Integer maxSeats,
    Integer currentSeats
) {
    public static FloorSeatsDto toDto(FloorPlan floorPlan) {
        return FloorSeatsDto.builder()
            .maxSeats(floorPlan.getMaxSeats())
            .currentSeats(floorPlan.getCurrentSeats())
            .build();
    }
}
