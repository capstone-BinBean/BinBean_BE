package binbean.binbean_BE.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record FloorList(List<Position> borderPosition, List<Position> seatPosition,
                        List<Position> doorPosition, List<Position> counterPosition,
                        List<Position> toiletPosition, List<Position> windowPosition) {

}
