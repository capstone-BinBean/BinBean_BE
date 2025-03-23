package binbean.binbean_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@Table(name = "BUSINESS_HOURS_TB")
public class BusinessHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_hours_id", nullable = false)
    private Long id;

    @Column(name = "monday_start", nullable = false)
    private String mondayStart;

    @Column(name = "monday_end", nullable = false)
    private String mondayEnd;

    @Column(name = "tuesday_start", nullable = false)
    private String tuesdayStart;

    @Column(name = "tuesday_end", nullable = false)
    private String tuesdayEnd;

    @Column(name = "wednesday_start", nullable = false)
    private String wednesdayStart;

    @Column(name = "wednesday_end", nullable = false)
    private String wednesdayEnd;

    @Column(name = "thursday_start", nullable = false)
    private String thursdayStart;

    @Column(name = "thursday_end", nullable = false)
    private String thursdayEnd;

    @Column(name = "friday_start", nullable = false)
    private String fridayStart;

    @Column(name = "friday_end", nullable = false)
    private String fridayEnd;

    @Column(name = "saturday_start", nullable = false)
    private String saturdayStart;

    @Column(name = "saturday_end", nullable = false)
    private String saturdayEnd;

    @Column(name = "sunday_start", nullable = false)
    private String sundayStart;

    @Column(name = "sunday_end", nullable = false)
    private String sundayEnd;
}
