package binbean.binbean_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Entity
@Table(name = "FAVORITES_TB")
public class Favorites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Column(name = "is_favorites", nullable = false)
    private Boolean isFavorites;

    @Column(name = "seats_number", nullable = false)
    private Long seatsNumber;

    public void setIsFavorites(Boolean isFavorites) {
        this.isFavorites = isFavorites;
    }

    public static Favorites create(User user, Cafe cafe, Long seatsNumber){
        return Favorites.builder()
            .user(user)
            .cafe(cafe)
            .isFavorites(true)
            .seatsNumber(seatsNumber)
            .build();
    }
}
