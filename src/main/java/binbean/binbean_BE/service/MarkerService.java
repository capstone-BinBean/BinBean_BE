package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.marker.CafeMarkerDto;
import binbean.binbean_BE.entity.user.User;
import binbean.binbean_BE.repository.CafeRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MarkerService {

    private final CafeRepository cafeRepository;

    public MarkerService(CafeRepository cafeRepository) {
        this.cafeRepository = cafeRepository;
    }

    public List<CafeMarkerDto> getMarkers(User currentUser) {
        // FIXME : 특정 지역(예: 사용자의 현재 위치 기준 반경 1km)
        return cafeRepository.findAll().stream().map(CafeMarkerDto::toDto).toList();
    }
}
