package binbean.binbean_BE.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record FloorList(List<Position> borderPosition, List<Position> seatPosition,
                        List<Position> doorPosition, List<Position> counterPosition,
                        List<Position> toiletPosition, List<Position> windowPosition) {

    public static FloorList create(List<Position> borderPosition, List<Position> seatPosition,
        List<Position> doorPosition, List<Position> counterPosition, List<Position> toiletPosition,
        List<Position> windowPosition
    ) {
        return FloorList.builder()
            .borderPosition(borderPosition)
            .seatPosition(seatPosition)
            .doorPosition(doorPosition)
            .counterPosition(counterPosition)
            .toiletPosition(toiletPosition)
            .windowPosition(windowPosition)
            .build();
    }
}
