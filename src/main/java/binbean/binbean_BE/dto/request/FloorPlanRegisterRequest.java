package binbean.binbean_BE.dto.request;

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

public record FloorPlanRegisterRequest(List<FloorInfo> floorList) {

    public record FloorInfo(List<Position> borderPosition, List<Position> seatPosition,
                            List<Position> doorPosition, List<Position> counterPosition,
                            List<Position> toiletPosition, List<Position> windowPosition, int maxSeats,
                            int floorNumber) {

        public FloorPlan toFloorPlanEntity(Cafe cafe) {
            return FloorPlan.builder()
                .cafe(cafe)
                .floorNumber(floorNumber)
                .maxSeats(maxSeats)
                .build();
        }

        public List<BorderLine> toBorderLinesEntity(FloorPlan floorPlan) {
            return borderPosition.stream()
                .map(pos -> BorderLine.builder()
                    .floorPlan(floorPlan)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Seats> toSeatsEntities(FloorPlan floorPlan) {
            return seatPosition.stream()
                .map(pos -> Seats.builder()
                    .floorPlan(floorPlan)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Door> toDoorsEntity(FloorPlan floorPlan) {
            return doorPosition.stream()
                .map(pos -> Door.builder()
                    .floorPlan(floorPlan)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Counter> toCountersEntity(FloorPlan floorPlan) {
            return counterPosition.stream()
                .map(pos -> Counter.builder()
                    .floorPlan(floorPlan)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Toilet> toToiletsEntity(FloorPlan floorPlan) {
            return toiletPosition.stream()
                .map(pos -> Toilet.builder()
                    .floorPlan(floorPlan)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Window> toWindowsEntity(FloorPlan floorPlan) {
            return windowPosition.stream()
                .map(pos -> Window.builder()
                    .floorPlan(floorPlan)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public record Position(double x, double y) {}
    }
}
