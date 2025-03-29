package binbean.binbean_BE.dto.marker;

import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import java.util.List;
import lombok.Builder;

// FIXME : cafeName, latitude, longitude로 수정
@Builder
public record CafeMarkerDto(
    Long cafeId,
    String cafeAddress,
    List<FloorSeatsDto> floorSeats
) {
    public static CafeMarkerDto toDto(Cafe cafe, List<FloorPlan> floorList) {
        return CafeMarkerDto.builder()
            .cafeId(cafe.getId())
            .cafeAddress(cafe.getCafeAddress())
            .floorSeats(floorList.stream().map(FloorSeatsDto::toDto).toList())
            .build();
    }
}
