package binbean.binbean_BE.entity;

import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    /*
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

*/

    @OneToOne
    @JoinColumn(name = "business_hours_id", nullable = false)
    private BusinessHours businessHours;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FloorPlan> floorList;

    @Column(name = "cafe_name", nullable = false)
    private String cafeName;

    @Column(name = "cafe_address", nullable = false)
    private String cafeAddress;

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
    public Cafe(Long id, BusinessHours businessHours, List<FloorPlan> floorList, String cafeName, String cafeAddress,
        String cafePhone, String cafeDescription, int wifiAvailable, int petAvailable,
        int kidsAvailable, int chargeAvailable) {
        this.id = id;
        this.businessHours = businessHours;
        this.floorList = floorList;
        this.cafeName = cafeName;
        this.cafeAddress = cafeAddress;
        this.cafePhone = cafePhone;
        this.cafeDescription = cafeDescription;
        this.wifiAvailable = wifiAvailable;
        this.petAvailable = petAvailable;
        this.kidsAvailable = kidsAvailable;
        this.chargeAvailable = chargeAvailable;
    }
}
