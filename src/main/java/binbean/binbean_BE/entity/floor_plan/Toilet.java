package binbean.binbean_BE.entity.floor_plan;

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
@Builder
@Table(name = "TOILET_TB")
public class Toilet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toilet_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "floor_plan_id", nullable = false)
    private FloorPlan floorPlan;

    @Column(name = "x", nullable = false)
    private double x;

    @Column(name = "y", nullable = false)
    private double y;
}
