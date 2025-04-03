package binbean.binbean_BE.util;

import binbean.binbean_BE.dto.request.FloorPlanUpdateRequest;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.floor_plan.BorderLine;
import binbean.binbean_BE.entity.floor_plan.Counter;
import binbean.binbean_BE.entity.floor_plan.Door;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.entity.floor_plan.Seats;
import binbean.binbean_BE.entity.floor_plan.Toilet;
import binbean.binbean_BE.entity.floor_plan.Window;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FloorPlanMapper {
    public FloorPlan toFloorPlan(FloorPlanUpdateRequest dto, Cafe cafe) {
        return FloorPlan.builder()
            .cafe(cafe)
            .floorNumber(dto.floorNumber())
            .maxSeats(dto.maxSeats())
            .build();
    }

    public List<BorderLine> toBorderLines(FloorPlanUpdateRequest.FloorList floorList, FloorPlan floorPlan) {
        return floorList.borderPosition().stream()
            .map(pos -> BorderLine.builder()
                .floorPlan(floorPlan)
                .x(pos.x())
                .y(pos.y())
                .build())
            .collect(Collectors.toList());
    }

    public List<Seats> toSeats(FloorPlanUpdateRequest.FloorList floorList, FloorPlan floorPlan) {
        return floorList.seatPosition().stream()
            .map(pos -> Seats.builder()
                .floorPlan(floorPlan)
                .x(pos.x())
                .y(pos.y())
                .build())
            .collect(Collectors.toList());
    }

    public List<Door> toDoors(FloorPlanUpdateRequest.FloorList floorList, FloorPlan floorPlan) {
        return floorList.doorPosition().stream()
            .map(pos -> Door.builder()
                .floorPlan(floorPlan)
                .x(pos.x())
                .y(pos.y())
                .build())
            .collect(Collectors.toList());
    }

    public List<Counter> toCounters(FloorPlanUpdateRequest.FloorList floorList, FloorPlan floorPlan) {
        return floorList.counterPosition().stream()
            .map(pos -> Counter.builder()
                .floorPlan(floorPlan)
                .x(pos.x())
                .y(pos.y())
                .build())
            .collect(Collectors.toList());
    }

    public List<Toilet> toToilets(FloorPlanUpdateRequest.FloorList floorList, FloorPlan floorPlan) {
        return floorList.toiletPosition().stream()
            .map(pos -> Toilet.builder()
                .floorPlan(floorPlan)
                .x(pos.x())
                .y(pos.y())
                .build())
            .collect(Collectors.toList());
    }

    public List<Window> toWindows(FloorPlanUpdateRequest.FloorList floorList, FloorPlan floorPlan) {
        return floorList.windowPosition().stream()
            .map(pos -> Window.builder()
                .floorPlan(floorPlan)
                .x(pos.x())
                .y(pos.y())
                .build())
            .collect(Collectors.toList());
    }
}
