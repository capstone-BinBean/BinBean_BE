package binbean.binbean_BE.dto;

import lombok.Builder;

@Builder
public record ObjectId(Long id) {

    public static ObjectId create(Long id) {

        return ObjectId.builder()
            .id(id)
            .build();
    }
}
