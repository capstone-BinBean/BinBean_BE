package binbean.binbean_BE.dto.marker;

import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import lombok.Builder;

@Builder
public record FloorSeatsResponse(
    Integer maxSeats,
    Integer currentSeats
) {
    public static FloorSeatsResponse toDto(FloorPlan floorPlan) {
        return FloorSeatsResponse.builder()
            .maxSeats(floorPlan.getMaxSeats())
            .currentSeats(floorPlan.getCurrentSeats())
            .build();
    }
}
