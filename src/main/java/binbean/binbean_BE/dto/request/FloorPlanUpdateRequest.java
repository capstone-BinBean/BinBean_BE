package binbean.binbean_BE.dto.request;

import binbean.binbean_BE.dto.FloorList;
import java.util.List;

public record FloorPlanUpdateRequest(FloorList floorList, int floorNumber, int maxSeats) {}
