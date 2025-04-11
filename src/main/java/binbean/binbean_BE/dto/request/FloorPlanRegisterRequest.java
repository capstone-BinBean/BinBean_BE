package binbean.binbean_BE.dto.request;

import binbean.binbean_BE.dto.FloorList;

public record FloorPlanRegisterRequest(FloorList floorList, int floorNumber, int maxSeats) {

}
