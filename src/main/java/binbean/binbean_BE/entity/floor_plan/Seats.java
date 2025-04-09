package binbean.binbean_BE.entity.floor_plan;

import binbean.binbean_BE.dto.Position;
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

@Entity
@Getter
@Table(name = "SEATS_TB")
public class Seats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seats_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "floor_plan_id", nullable = false)
    private FloorPlan floorPlan;

    @Column(name = "x", nullable = false)
    private double x;

    @Column(name = "y", nullable = false)
    private double y;

    protected Seats() {
    }

    @Builder
    public Seats(FloorPlan floorPlan, double x, double y) {
        this.floorPlan = floorPlan;
        this.x = x;
        this.y = y;
    }

    public static Seats create(FloorPlan floorPlan, double x, double y) {
        return Seats.builder()
            .floorPlan(floorPlan)
            .x(x)
            .y(y)
            .build();
    }

    public Position toPositionEntity() {
        return Position.builder()
            .x(this.x)
            .y(this.y)
            .build();
    }
}
