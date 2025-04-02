package binbean.binbean_BE.dto.request;

import binbean.binbean_BE.entity.BusinessHours;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.User;
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

    public Cafe toCafeEntity(User user) {
        return Cafe.builder()
            .user(user)
            .cafeName(this.cafeName)
            .cafeAddress(this.cafeAddress)
            .cafePhone(this.cafePhone)
            .wifiAvailable(this.wifiAvailable)
            .chargeAvailable(this.chargerAvailable)
            .kidsAvailable(this.kidsAvailable)
            .petAvailable(this.petAvailable)
            .cafeDescription(this.cafeDescription)
            .build();
    }

    public BusinessHours toBusinessHoursEntity() {
        return BusinessHours.builder()
            .mondayStart(this.monday_start)
            .mondayEnd(this.monday_end)
            .tuesdayStart(this.tuesday_start)
            .tuesdayEnd(this.tuesday_end)
            .wednesdayStart(this.wednesday_start)
            .wednesdayEnd(this.wednesday_end)
            .thursdayStart(this.thursday_start)
            .thursdayEnd(this.thursday_end)
            .fridayStart(this.friday_start)
            .fridayEnd(this.friday_end)
            .saturdayStart(this.saturday_start)
            .saturdayEnd(this.saturday_end)
            .sundayStart(this.sunday_start)
            .sundayEnd(this.sunday_end)
            .build();
    }
}
