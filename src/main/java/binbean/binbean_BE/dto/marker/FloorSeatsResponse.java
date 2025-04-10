package binbean.binbean_BE.dto.marker;

import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import lombok.Builder;

@Builder
public record FloorSeatsResponse(
    int maxSeats,
    int currentSeats
) {
    public static FloorSeatsResponse create(FloorPlan floorPlan) {
        return FloorSeatsResponse.builder()
            .maxSeats(floorPlan.getMaxSeats())
            .currentSeats(floorPlan.getCurrentSeats())
            .build();
    }
}
