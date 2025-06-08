package binbean.binbean_BE.util;

import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.entity.floor_plan.BorderLine;
import binbean.binbean_BE.entity.floor_plan.Counter;
import binbean.binbean_BE.entity.floor_plan.Door;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.entity.floor_plan.Seats;
import binbean.binbean_BE.entity.floor_plan.Table;
import binbean.binbean_BE.entity.floor_plan.Toilet;
import binbean.binbean_BE.entity.floor_plan.Window;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FloorPlanMapper {

    public List<BorderLine> createBorderLines(FloorList floorList, FloorPlan floorPlan) {
        return floorList.borderPosition().stream()
            .map(pos -> BorderLine.create(floorPlan, pos.x(), pos.y()))
            .collect(Collectors.toList());
    }

    public List<Seats> createSeats(FloorList floorList, FloorPlan floorPlan) {
        return floorList.seatPosition().stream()
            .map(pos -> Seats.create(floorPlan, pos.x(), pos.y()))
            .collect(Collectors.toList());
    }

    public List<Door> createDoors(FloorList floorList, FloorPlan floorPlan) {
        return floorList.doorPosition().stream()
            .map(pos -> Door.create(floorPlan, pos.x(), pos.y()))
            .collect(Collectors.toList());
    }

    public List<Counter> createCounters(FloorList floorList, FloorPlan floorPlan) {
        return floorList.counterPosition().stream()
            .map(pos -> Counter.create(floorPlan, pos.x(), pos.y()))
            .collect(Collectors.toList());
    }

    public List<Toilet> createToilets(FloorList floorList, FloorPlan floorPlan) {
        return floorList.toiletPosition().stream()
            .map(pos -> Toilet.create(floorPlan, pos.x(), pos.y()))
            .collect(Collectors.toList());
    }

    public List<Window> createWindows(FloorList floorList, FloorPlan floorPlan) {
        return floorList.windowPosition().stream()
            .map(pos -> Window.create(floorPlan, pos.x(), pos.y()))
            .collect(Collectors.toList());
    }

    public List<Table> createTables(FloorList floorList, FloorPlan floorPlan) {
        return floorList.tablePosition().stream()
            .map(pos -> Table.create(floorPlan, pos.x(), pos.y()))
            .collect(Collectors.toList());
    }
}
