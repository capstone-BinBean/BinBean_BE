package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.marker.CafeMarkerDto;
import binbean.binbean_BE.entity.user.User;
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

    public List<CafeMarkerDto> getMarkers(User currentUser) {
        // FIXME : 특정 지역(예: 사용자의 현재 위치 기준 반경 1km)
        var cafes = cafeRepository.findAll();

        return cafes.stream()
            .map( cafe -> {
                var floorList = floorPlanRepository.findByCafeId(cafe.getId());
                return CafeMarkerDto.toDto(cafe, floorList);
            })
            .toList();
    }
}
