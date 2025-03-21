package binbean.binbean_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_id", nullable = false)
    private Long id;

    /*
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "business_hour_id", nullable = false)
    private BusinessHour businessHour;
     */

    @OneToOne
    @JoinColumn(name = "business_hours_id", nullable = false)
    private BusinessHours businessHours;

    @Column(name = "cafe_name", nullable = false)
    private String cafeName;

    @Column(name = "cafe_address", nullable = false)
    private String cafeAddress;

    @Column(name = "cafe_phone", nullable = false)
    private String cafePhone;

    @Column(name = "cafe_description", nullable = false)
    private String cafeDescription;

    @Column(name = "max_seats", nullable = false)
    private int maxSeats;

    @Column(name = "wife_available", nullable = false)
    private int wifiAvailable;

    @Column(name = "pet_available", nullable = false)
    private int petAvailable;

    @Column(name = "kids_available", nullable = false)
    private int kidsAvailable;

    @Column(name = "charge_available", nullable = false)
    private int chargeAvailable;

    protected Cafe() {}
}
