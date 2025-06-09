package binbean.binbean_BE.entity.floor_plan;

import binbean.binbean_BE.dto.CurrentSeats;
import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.ObjectId;
import binbean.binbean_BE.dto.Position;
import binbean.binbean_BE.dto.response.FloorPlanResponse;
import binbean.binbean_BE.entity.Cafe;
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
@Table(name = "FLOOR_PLAN_TB")
public class FloorPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "floor_plan_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Column(name = "floor_number", nullable = false)
    private int floorNumber;

    @Column(name = "max_seats", nullable = false)
    private int maxSeats;

    @Column(name = "current_seats")
    private int currentSeats;

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    protected FloorPlan() {
    }

    @Builder
    public FloorPlan(Cafe cafe, int floorNumber, int maxSeats) {
        this.cafe = cafe;
        this.floorNumber = floorNumber;
        this.maxSeats = maxSeats;
    }

    public static FloorPlan create(Cafe cafe, int floorNumber, int maxSeats) {
        return FloorPlan.builder()
            .cafe(cafe)
            .floorNumber(floorNumber)
            .maxSeats(maxSeats)
            .build();
    }

    public FloorPlanResponse toFloorPlanDto(List<Position> borderPositions, List<Position> seatPositions,
        List<Position> doorPositions, List<Position> counterPositions, List<Position> toiletPositions,
        List<Position> windowPositions, List<Position> tablePositions, List<Position> currentPositions) {

        FloorList floorList = FloorList.create(borderPositions, seatPositions, doorPositions,
            counterPositions, toiletPositions, windowPositions, tablePositions);

        CurrentSeats currentSeats = CurrentSeats.create(currentPositions);

        return FloorPlanResponse.create(floorList, this.floorNumber, currentSeats);
    }

    public ObjectId toObjectIdDto() {

        return ObjectId.create(this.id);
    }
}
