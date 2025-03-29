package binbean.binbean_BE.entity;

import binbean.binbean_BE.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
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
}
