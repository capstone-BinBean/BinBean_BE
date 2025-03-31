package binbean.binbean_BE.dto.marker;

import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import java.util.List;
import lombok.Builder;

@Builder
public record CafeMarkerDto(
    Long cafeId,
    String cafeName,
    Double latitude,
    Double longitude,
    List<FloorSeatsDto> floorSeats
) {
    public static CafeMarkerDto toDto(Cafe cafe, List<FloorPlan> floorList) {
        return CafeMarkerDto.builder()
            .cafeId(cafe.getId())
            .cafeName(cafe.getCafeName())
            .latitude(cafe.getLatitude())
            .longitude(cafe.getLongitude())
            .floorSeats(floorList.stream().map(FloorSeatsDto::toDto).toList())
            .build();
    }
}
