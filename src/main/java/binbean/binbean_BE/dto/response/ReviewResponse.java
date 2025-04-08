package binbean.binbean_BE.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ReviewResponse(LocalDateTime createdAt, String reviewer, String reviewerProfileUrl,
                             String reviewText, double reviewScore, List<String> reviewImgUrl) {

    public static ReviewResponse create(LocalDateTime createdAt, String nickname, String profile,
        String reviewText, double reviewScore, List<String> reviewImgUrl) {
        return ReviewResponse.builder()
            .createdAt(createdAt)
            .reviewer(nickname)
            .reviewerProfileUrl(profile)
            .reviewText(reviewText)
            .reviewScore(reviewScore)
            .reviewImgUrl(reviewImgUrl)
            .build();
    }
}
