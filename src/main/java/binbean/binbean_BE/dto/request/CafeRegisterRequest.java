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

public record CafeRegisterRequest(String cafeName, String cafeAddress, double latitude, double longitude,
                                  String cafePhone, int wifiAvailable, int chargerAvailable,
                                  int kidsAvailable, int petAvailable, String cafeDescription,
                                  String monday_start, String monday_end, String tuesday_start,
                                  String tuesday_end, String wednesday_start, String wednesday_end,
                                  String thursday_start, String thursday_end, String friday_start,
                                  String friday_end, String saturday_start, String saturday_end,
                                  String sunday_start, String sunday_end) {

    public Cafe toCafeEntity(User user) {

        return Cafe.create(user, this.cafeName, this.cafeAddress, this.latitude, this.longitude,
            this.cafePhone, this.wifiAvailable, this.chargerAvailable, this.kidsAvailable, this.petAvailable,
            this.cafeDescription);
    }

    public BusinessHours toBusinessHoursEntity(Cafe cafe) {

        return BusinessHours.create(cafe, this.monday_start, this.monday_end, this.tuesday_start, this.tuesday_end,
            this.wednesday_start, this.wednesday_end, this.thursday_start, this.thursday_end,
            this.friday_start, this.friday_end, this.saturday_start, this.saturday_end, this.sunday_start,
            this.sunday_end);
    }
}
