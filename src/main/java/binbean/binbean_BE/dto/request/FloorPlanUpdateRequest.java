package binbean.binbean_BE.dto.request;

import java.util.List;

public record FloorPlanUpdateRequest(FloorList floorList, int floorNumber, int maxSeats) {

    public record FloorList(List<Position> borderPosition, List<Position> seatPosition,
        List<Position> doorPosition, List<Position> counterPosition, List<Position> toiletPosition,
        List<Position> windowPosition
    ) {}

    public record Position(double x, double y) {}
}
