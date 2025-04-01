package binbean.binbean_BE.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ReviewResponse(LocalDateTime createAt, String reviewer, String reviewerProfileUrl,
                             String reviewText, double reviewScore, List<String> reviewImgUrl) {}
