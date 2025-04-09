package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.marker.CafeMarkerResponse;
import binbean.binbean_BE.dto.request.CafeFindRequest;
import binbean.binbean_BE.repository.CafeRepository;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MarkerService {

    private final CafeRepository cafeRepository;
    private final FloorPlanRepository floorPlanRepository;

    public MarkerService(CafeRepository cafeRepository, FloorPlanRepository floorPlanRepository) {
        this.cafeRepository = cafeRepository;
        this.floorPlanRepository = floorPlanRepository;
    }

    public List<CafeMarkerResponse> getMarkers(CafeFindRequest request) {
        // 사용자의 현재 위치 기준 또는 검색하는 위치 기준 반경 1km 내의 카페를 검색
        var cafes = cafeRepository.findCafesWithinRadius(request.latitude(), request.longitude(), 1);

        return cafes.stream()
            .map( cafe -> {
                var floorList = floorPlanRepository.findByCafeId(cafe.getId());
                return CafeMarkerResponse.create(cafe, floorList);
            })
            .toList();
    }
}
