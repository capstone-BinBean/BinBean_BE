package binbean.binbean_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA만 사용 가능하도록 제한
@Table(name = "MARKER_TB")
public class Marker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marker_id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Column(name = "cafe_address")
    private String cafeAddress;

    @Column(name = "max_seats")
    private int maxSeats;

    @Column(name = "current_seats")
    private int currentSeats;

    @Builder
    public Marker(Long id, Cafe cafe, String cafeAddress, int maxSeats, int currentSeats) {
        this.id = id;
        this.cafe = cafe;
        this.cafeAddress = cafeAddress;
        this.maxSeats = maxSeats;
        this.currentSeats = currentSeats;
    }
}
