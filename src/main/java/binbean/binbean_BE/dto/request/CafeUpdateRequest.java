package binbean.binbean_BE.dto.request;

public record CafeUpdateRequest(String cafeName, String cafeAddress, String cafePhone,
                                int wifiAvailable, int chargerAvailable, int kidsAvailable, int petAvailable,
                                String cafeDescription, String mondayStart, String mondayEnd,
                                String tuesdayStart, String tuesdayEnd, String wednesdayStart,
                                String wednesdayEnd, String thursdayStart, String thursdayEnd,
                                String fridayStart, String fridayEnd, String saturdayStart,
                                String saturdayEnd, String sundayStart, String sundayEnd) {

}
