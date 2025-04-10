package binbean.binbean_BE.dto.response;

import lombok.Builder;

@Builder
public record CafeSearchResponse(Long cafeId, String cafeName, String cafeAddress, String cafeDescription) {

    public static CafeSearchResponse create(Long cafeId, String cafeName, String cafeAddress,
        String cafeDescription) {

        return CafeSearchResponse.builder()
            .cafeId(cafeId)
            .cafeName(cafeName)
            .cafeAddress(cafeAddress)
            .cafeDescription(cafeDescription)
            .build();
    }
}
