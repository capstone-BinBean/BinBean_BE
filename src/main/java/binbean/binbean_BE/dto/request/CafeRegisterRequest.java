package binbean.binbean_BE.dto.request;

import binbean.binbean_BE.entity.BusinessHours;
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

public record CafeRegisterRequest(String cafeName, String cafeAddress, String cafePhone, int wifiAvailable,
                                  int chargerAvailable, int kidsAvailable, int petAvailable,
                                  String cafeDescription, List<FloorInfo> floorList, String monday_start,
                                  String monday_end, String tuesday_start, String tuesday_end,
                                  String wednesday_start, String wednesday_end, String thursday_start,
                                  String thursday_end, String friday_start, String friday_end,
                                  String saturday_start, String saturday_end, String sunday_start,
                                  String sunday_end) {

    public Cafe toEntity(BusinessHours businessHours) {
        return Cafe.builder()
            .cafeName(cafeName)
            .cafeAddress(cafeAddress)
            .cafePhone(cafePhone)
            .wifiAvailable(wifiAvailable)
            .chargeAvailable(chargerAvailable)
            .kidsAvailable(kidsAvailable)
            .petAvailable(petAvailable)
            .cafeDescription(cafeDescription)
            .businessHours(businessHours)
            .build();
    }

    public record FloorInfo(List<Position> borderPosition, List<Position> seatPosition,
                            List<Position> doorPosition, List<Position> counterPosition,
                            List<Position> toiletPosition, List<Position> windowPosition, int maxSeats,
                            int floorNumber) {

        public FloorPlan toFloorPlanEntity(Cafe cafe) {
            return FloorPlan.builder()
                .cafe(cafe)
                .floorNumber(floorNumber)
                .build();
        }

        public List<BorderLine> toBorderLinesEntity(Cafe cafe) {
            return borderPosition.stream()
                .map(pos -> BorderLine.builder()
                    .cafe(cafe)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Seats> toSeatsEntities(Cafe cafe) {
            return seatPosition.stream()
                .map(pos -> Seats.builder()
                    .cafe(cafe)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Door> toDoorsEntity(Cafe cafe) {
            return doorPosition.stream()
                .map(pos -> Door.builder()
                    .cafe(cafe)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Counter> toCountersEntity(Cafe cafe) {
            return counterPosition.stream()
                .map(pos -> Counter.builder()
                    .cafe(cafe)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Toilet> toToiletsEntity(Cafe cafe) {
            return toiletPosition.stream()
                .map(pos -> Toilet.builder()
                    .cafe(cafe)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public List<Window> toWindowsEntity(Cafe cafe) {
            return windowPosition.stream()
                .map(pos -> Window.builder()
                    .cafe(cafe)
                    .x(pos.x())
                    .y(pos.y())
                    .build())
                .collect(Collectors.toList());
        }

        public record Position(double x, double y) {}
    }
}
