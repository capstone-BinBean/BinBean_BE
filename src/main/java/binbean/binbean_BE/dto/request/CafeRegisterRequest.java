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
                                  String cafeDescription, String monday_start,
                                  String monday_end, String tuesday_start, String tuesday_end,
                                  String wednesday_start, String wednesday_end, String thursday_start,
                                  String thursday_end, String friday_start, String friday_end,
                                  String saturday_start, String saturday_end, String sunday_start,
                                  String sunday_end) {

    public Cafe toCafeEntity() {
        return Cafe.builder()
            .cafeName(cafeName)
            .cafeAddress(cafeAddress)
            .cafePhone(cafePhone)
            .wifiAvailable(wifiAvailable)
            .chargeAvailable(chargerAvailable)
            .kidsAvailable(kidsAvailable)
            .petAvailable(petAvailable)
            .cafeDescription(cafeDescription)
            .build();
    }

    public BusinessHours toBusinessHoursEntity() {
        return BusinessHours.builder()
            .mondayStart(monday_start)
            .mondayEnd(monday_end)
            .tuesdayStart(tuesday_start)
            .tuesdayEnd(tuesday_end)
            .wednesdayStart(wednesday_start)
            .wednesdayEnd(wednesday_end)
            .thursdayStart(thursday_start)
            .thursdayEnd(thursday_end)
            .fridayStart(friday_start)
            .fridayEnd(friday_end)
            .saturdayStart(saturday_start)
            .saturdayEnd(saturday_end)
            .sundayStart(sunday_start)
            .sundayEnd(sunday_end)
            .build();
    }
}
