package binbean.binbean_BE.entity;

import binbean.binbean_BE.dto.request.CafeUpdateRequest;
import binbean.binbean_BE.dto.response.CafeInfoResponse;
import binbean.binbean_BE.dto.response.ReviewResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "CAFE_TB")
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "cafe_name", nullable = false)
    private String cafeName;

    @Column(name = "cafe_address", nullable = false)
    private String cafeAddress;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "cafe_phone", nullable = false)
    private String cafePhone;

    @Column(name = "cafe_description", nullable = false)
    private String cafeDescription;

    @Column(name = "wife_available", nullable = false)
    private int wifiAvailable;

    @Column(name = "pet_available", nullable = false)
    private int petAvailable;

    @Column(name = "kids_available", nullable = false)
    private int kidsAvailable;

    @Column(name = "charge_available", nullable = false)
    private int chargerAvailable;

    protected Cafe() {
    }

    @Builder
    public static Cafe create(User user, String cafeName, String cafeAddress, String cafePhone,
        int wifiAvailable, int chargerAvailable, int kidsAvailable, int petAvailable,
        String cafeDescription) {

        return Cafe.builder()
            .user(user)
            .cafeName(cafeName)
            .cafeAddress(cafeAddress)
            .cafePhone(cafePhone)
            .wifiAvailable(wifiAvailable)
            .chargerAvailable(chargerAvailable)
            .kidsAvailable(kidsAvailable)
            .petAvailable(petAvailable)
            .cafeDescription(cafeDescription)
            .build();
    }

    public CafeInfoResponse toCafeDto(String startTime, String endTime, double reviewAvg,
        List<String> cafeImgUrl, List<ReviewResponse> reviewResponse, List<Long> floorPlanIds) {

        return CafeInfoResponse.create(this.id, this.cafeName, this.cafeAddress, startTime, endTime,
            this.cafePhone, this.wifiAvailable, this.chargerAvailable, this.petAvailable, this.kidsAvailable,
            this.cafeDescription, cafeImgUrl, reviewAvg, reviewResponse, floorPlanIds);
    }

    public void update(CafeUpdateRequest request) {
        this.cafeName = request.cafeName();
        this.cafeAddress = request.cafeAddress();
        this.cafePhone = request.cafePhone();
        this.wifiAvailable = request.wifiAvailable();
        this.chargerAvailable = request.chargerAvailable();
        this.petAvailable = request.petAvailable();
        this.kidsAvailable = request.kidsAvailable();
        this.cafeDescription = request.cafeDescription();
    }
}
