package binbean.binbean_BE.dto.request;

import binbean.binbean_BE.entity.BusinessHours;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.User;

public record CafeRegisterRequest(String cafeName, String cafeAddress, String cafePhone, double latitude,
                                  double longitude, int wifiAvailable, int chargerAvailable, int kidsAvailable,
                                  int petAvailable, String cafeDescription, String mondayStart,
                                  String mondayEnd, String tuesdayStart, String tuesdayEnd,
                                  String wednesdayStart, String wednesdayEnd, String thursdayStart,
                                  String thursdayEnd, String fridayStart, String fridayEnd,
                                  String saturdayStart, String saturdayEnd, String sundayStart,
                                  String sundayEnd) {

    public Cafe toCafeEntity(User user) {

        return Cafe.create(user, this.cafeName, this.cafeAddress, this.latitude, this.longitude,
            this.cafePhone, this.wifiAvailable, this.chargerAvailable, this.kidsAvailable, this.petAvailable,
            this.cafeDescription);
    }

    public BusinessHours toBusinessHoursEntity(Cafe cafe) {

        return BusinessHours.create(cafe, this.mondayStart, this.mondayEnd, this.tuesdayStart, this.tuesdayEnd,
            this.wednesdayStart, this.wednesdayEnd, this.thursdayStart, this.thursdayEnd,
            this.fridayStart, this.fridayEnd, this.saturdayStart, this.saturdayEnd, this.sundayStart,
            this.sundayEnd);
    }
}
