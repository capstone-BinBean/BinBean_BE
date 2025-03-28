package binbean.binbean_BE.dto.marker;

import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import lombok.Builder;

@Builder
public record CafeMarkerDto(
    Long cafeId,
    String cafeAddress,
    Integer maxSeats,
    Integer currentSeats
) {
    public static CafeMarkerDto toDto(Cafe cafe) {
        return CafeMarkerDto.builder()
            .cafeId(cafe.getId())
            .cafeAddress(cafe.getCafeAddress())
            .maxSeats(cafe.getFloorList().stream().mapToInt(FloorPlan::getMaxSeats).sum())
            .currentSeats(cafe.getFloorList().stream().mapToInt(FloorPlan::getMaxSeats).sum())
            .build();
    }
}
