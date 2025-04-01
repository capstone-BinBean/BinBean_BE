package binbean.binbean_BE.dto.marker;

import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import java.util.List;
import lombok.Builder;

@Builder
public record CafeMarkerResponse(
    Long cafeId,
    String cafeName,
    Double latitude,
    Double longitude,
    List<FloorSeatsResponse> floorSeats
) {
    public static CafeMarkerResponse toDto(Cafe cafe, List<FloorPlan> floorList) {
        return CafeMarkerResponse.builder()
            .cafeId(cafe.getId())
            .cafeName(cafe.getCafeName())
            .latitude(cafe.getLatitude())
            .longitude(cafe.getLongitude())
            .floorSeats(floorList.stream().map(FloorSeatsResponse::toDto).toList())
            .build();
    }
}
