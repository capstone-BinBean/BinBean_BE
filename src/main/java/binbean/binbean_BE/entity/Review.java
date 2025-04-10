package binbean.binbean_BE.entity;

import binbean.binbean_BE.dto.response.ReviewResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "REVIEW_TB")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "review_text", nullable = false)
    private String reviewText;

    @Column(name = "review_score", nullable = false)
    private double reviewScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    protected Review() {
    }

    @Builder
    public Review(Cafe cafe, User user, String reviewText, double reviewScore) {
        this.cafe = cafe;
        this.user = user;
        this.reviewText = reviewText;
        this.reviewScore = reviewScore;
    }

    public static Review create(Cafe cafe, User user, String reviewText, double reviewScore) {

        return Review.builder()
            .cafe(cafe)
            .user(user)
            .reviewText(reviewText)
            .reviewScore(reviewScore)
            .build();
    }

    public ReviewResponse toReviewDto(List<String> reviewImgUrls) {

        return ReviewResponse.create(this.createdAt, this.user.getNickname(), this.user.getProfile(),
            this.reviewText, this.reviewScore, reviewImgUrls);
    }
}
