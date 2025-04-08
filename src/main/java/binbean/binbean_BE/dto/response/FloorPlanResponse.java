package binbean.binbean_BE.dto.response;

import binbean.binbean_BE.dto.CurrentSeats;
import binbean.binbean_BE.dto.FloorList;
import lombok.Builder;

@Builder
public record FloorPlanResponse(FloorList floorList, int floorNumber, CurrentSeats currentSeats) {

    public static FloorPlanResponse create(FloorList floorList, int floorNumber, CurrentSeats currentSeats) {
        return FloorPlanResponse.builder()
            .floorList(floorList)
            .floorNumber(floorNumber)
            .currentSeats(currentSeats)
            .build();
    }
}
