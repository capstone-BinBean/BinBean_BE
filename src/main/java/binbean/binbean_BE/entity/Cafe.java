package binbean.binbean_BE.entity;

import binbean.binbean_BE.dto.ImgUrl;
import binbean.binbean_BE.dto.ObjectId;
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
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

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
    public Cafe(User user, String cafeName, String cafeAddress, double latitude, double longitude,
        String cafePhone, String cafeDescription, int wifiAvailable, int petAvailable, int kidsAvailable,
        int chargerAvailable) {
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
        this.chargerAvailable = chargerAvailable;
    }

    public static Cafe create(User user, String cafeName, String cafeAddress, double latitude,
        double longitude, String cafePhone, int wifiAvailable, int chargerAvailable, int kidsAvailable,
        int petAvailable, String cafeDescription) {

        return Cafe.builder()
            .user(user)
            .cafeName(cafeName)
            .cafeAddress(cafeAddress)
            .latitude(latitude)
            .longitude(longitude)
            .cafePhone(cafePhone)
            .wifiAvailable(wifiAvailable)
            .chargerAvailable(chargerAvailable)
            .kidsAvailable(kidsAvailable)
            .petAvailable(petAvailable)
            .cafeDescription(cafeDescription)
            .build();
    }

    public CafeInfoResponse toCafeDto(String startTime, String endTime, List<ImgUrl> cafeImgUrl,
        double reviewAvg, List<ReviewResponse> reviewResponse, List<ObjectId> floorPlanIds) {

        return CafeInfoResponse.create(this.id, this.cafeName, this.cafeAddress, this.latitude,
            this.longitude, startTime, endTime, this.cafePhone, this.wifiAvailable, this.chargerAvailable,
            this.petAvailable, this.kidsAvailable, this.cafeDescription, cafeImgUrl, reviewAvg,
            reviewResponse, floorPlanIds);
    }

    public void update(CafeUpdateRequest request) {
        this.cafeName = request.cafeName();
        this.cafeAddress = request.cafeAddress();
        this.cafePhone = request.cafePhone();
        this.latitude = request.latitude();
        this.longitude = request.longitude();
        this.wifiAvailable = request.wifiAvailable();
        this.chargerAvailable = request.chargerAvailable();
        this.petAvailable = request.petAvailable();
        this.kidsAvailable = request.kidsAvailable();
        this.cafeDescription = request.cafeDescription();
    }
}
