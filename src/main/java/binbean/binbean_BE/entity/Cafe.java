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
@Builder
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
    private int chargeAvailable;

    protected Cafe() {}

    @Builder
    public Cafe(Long id, User user, String cafeName, String cafeAddress,
        double latitude, double longitude, String cafePhone, String cafeDescription, int wifiAvailable, int petAvailable,
        int kidsAvailable, int chargeAvailable) {
        this.id = id;
        this.user = user;
        this.cafeName = cafeName;
        this.cafeAddress = cafeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cafePhone = cafePhone;
        this.cafeDescription = cafeDescription;
        this.wifiAvailable = wifiAvailable;
        this.petAvailable = petAvailable;
        this.kidsAvailable = kidsAvailable;
        this.chargeAvailable = chargeAvailable;
    }

    public CafeInfoResponse toCafeDto(String startTime, String endTime, double reviewAvg,
        List<String> cafeImgUrl, List<ReviewResponse> reviewResponse, List<Long> floorPlanIds) {
        return CafeInfoResponse.builder()
            .cafeId(this.id)
            .cafeName(this.cafeName)
            .cafeAddress(this.cafeAddress)
            .startTime(startTime)
            .endTime(endTime)
            .cafePhone(this.cafePhone)
            .reviewAvg(reviewAvg)
            .wifiAvailable(this.wifiAvailable)
            .chargeAvailable(this.chargeAvailable)
            .petAvailable(this.petAvailable)
            .kidsAvailable(this.kidsAvailable)
            .cafeDescription(this.cafeDescription)
            .cafeImgUrl(cafeImgUrl)
            .reviews(reviewResponse)
            .floorPlanId(floorPlanIds)
            .build();
    }

    public void update(CafeUpdateRequest request) {
        this.cafeName = request.cafeName();
        this.cafeAddress = request.cafeAddress();
        this.cafePhone = request.cafePhone();
        this.wifiAvailable = request.wifiAvailable();
        this.chargeAvailable = request.chargerAvailable();
        this.petAvailable = request.petAvailable();
        this.kidsAvailable = request.kidsAvailable();
        this.cafeDescription = request.cafeDescription();
    }
}
