package binbean.binbean_BE.dto.marker;

import binbean.binbean_BE.entity.Marker;
import lombok.Builder;

@Builder
public record MarkerDto(
    Long cafeId,
    String cafeAddress,
    Integer maxSeats,
    Integer currentSeats
) {
    public static MarkerDto toDto(Marker marker) {
        return MarkerDto.builder()
            .cafeId(marker.getCafe().getId())
            .cafeAddress(marker.getCafeAddress())
            .maxSeats(marker.getMaxSeats())
            .currentSeats(marker.getCurrentSeats())
            .build();
    }
}
