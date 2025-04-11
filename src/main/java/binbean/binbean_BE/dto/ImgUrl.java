package binbean.binbean_BE.dto;

import lombok.Builder;

@Builder
public record ImgUrl(String url) {

    public static ImgUrl create(String reviewImgUrl) {

        return ImgUrl.builder()
            .url(reviewImgUrl)
            .build();
    }
}
