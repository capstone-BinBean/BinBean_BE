package binbean.binbean_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class CafeImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Column(name = "cafe_img_url", nullable = true)
    private String cafeImgUrl;

    public static CafeImg toEntity(Cafe cafe, String cafeImgUrl) {
        return CafeImg.builder()
            .cafe(cafe)
            .cafeImgUrl(cafeImgUrl)
            .build();
    }
}
