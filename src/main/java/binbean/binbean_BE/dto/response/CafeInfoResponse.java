package binbean.binbean_BE.dto.response;

import binbean.binbean_BE.dto.ImgUrl;
import binbean.binbean_BE.dto.ObjectId;
import java.util.List;
import lombok.Builder;

@Builder
public record CafeInfoResponse(Long cafeId, String cafeName, String cafeAddress, double latitude,
                               double longitude, String startTime, String endTime, String cafePhone,
                               int wifiAvailable, int chargerAvailable, int petAvailable, int kidsAvailable,
                               String cafeDescription, List<ImgUrl> cafeImgUrl, double reviewAvg,
                               List<ReviewResponse> reviewResponse, List<ObjectId> floorPlanId) {

    public static CafeInfoResponse create(Long cafeId, String cafeName, String cafeAddress, double latitude,
        double longitude, String startTime, String endTime, String cafePhone, int wifiAvailable,
        int chargerAvailable, int petAvailable, int kidsAvailable, String cafeDescription,
        List<ImgUrl> cafeImgUrl, double reviewAvg, List<ReviewResponse> reviewResponse,
        List<ObjectId> floorPlanIds) {

        return CafeInfoResponse.builder()
            .cafeId(cafeId)
            .cafeName(cafeName)
            .cafeAddress(cafeAddress)
            .latitude(latitude)
            .longitude(longitude)
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
