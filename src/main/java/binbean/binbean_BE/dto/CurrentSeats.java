package binbean.binbean_BE.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record CurrentSeats(List<Position> currentPosition) {

}
