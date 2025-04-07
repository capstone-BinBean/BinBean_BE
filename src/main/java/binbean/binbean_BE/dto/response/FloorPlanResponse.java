package binbean.binbean_BE.dto.response;

import binbean.binbean_BE.dto.CurrentSeats;
import binbean.binbean_BE.dto.FloorList;
import lombok.Builder;

@Builder
public record FloorPlanResponse(FloorList floorList, int floorNumber, CurrentSeats currentSeats) {

}
