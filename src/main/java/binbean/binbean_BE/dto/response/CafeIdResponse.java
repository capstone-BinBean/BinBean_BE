package binbean.binbean_BE.dto.response;

import lombok.Builder;

@Builder
public record CafeIdResponse(Long cafeId) {

    public static CafeIdResponse create(Long cafeId) {

        return CafeIdResponse.builder()
            .cafeId(cafeId)
            .build();
    }
}
