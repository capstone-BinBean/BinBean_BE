package binbean.binbean_BE.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record CafeInfoResponse(Long cafeId, String cafeName, String cafeAddress, String startTime,
                               String endTime, String cafePhone, double reviewAvg, int wifiAvailable,
                               int chargeAvailable, int petAvailable, int kidsAvailable,
                               String cafeDescription, List<String> cafeImgUrl, Long floorPlanId) {}
