package binbean.binbean_BE.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record CafeInfoResponse(Long cafeId, String cafeName, String cafeAddress, String startTime,
                               String endTime, String cafePhone, int wifiAvailable, int chargerAvailable,
                               int petAvailable, int kidsAvailable, String cafeDescription,
                               List<String> cafeImgUrl, double reviewAvg, List<ReviewResponse> reviewResponse,
                               List<Long> floorPlanId) {

    public static CafeInfoResponse create(Long cafeId, String cafeName, String cafeAddress, String startTime,
        String endTime, String cafePhone, int wifiAvailable, int chargerAvailable, int petAvailable,
        int kidsAvailable, String cafeDescription, List<String> cafeImgUrl, double reviewAvg,
        List<ReviewResponse> reviewResponse, List<Long> floorPlanIds) {

        return CafeInfoResponse.builder()
            .cafeId(cafeId)
            .cafeName(cafeName)
            .cafeAddress(cafeAddress)
            .startTime(startTime)
            .endTime(endTime)
            .cafePhone(cafePhone)
            .wifiAvailable(wifiAvailable)
            .chargerAvailable(chargerAvailable)
            .petAvailable(petAvailable)
            .kidsAvailable(kidsAvailable)
            .cafeDescription(cafeDescription)
            .cafeImgUrl(cafeImgUrl)
            .reviewAvg(reviewAvg)
            .reviewResponse(reviewResponse)
            .floorPlanId(floorPlanIds)
            .build();
    }
}
