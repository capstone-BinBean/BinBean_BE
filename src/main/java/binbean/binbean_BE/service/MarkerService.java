package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.marker.MarkerDto;
import binbean.binbean_BE.entity.Marker;
import binbean.binbean_BE.entity.user.User;
import binbean.binbean_BE.repository.MarkerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MarkerService {

    private final MarkerRepository markerRepository;

    public MarkerService(MarkerRepository markerRepository) {
        this.markerRepository = markerRepository;
    }

    public List<MarkerDto> getMarkers(User currentUser) {
        // FIXME : 특정 지역(예: 사용자의 현재 위치 기준 반경 1km)
        return markerRepository.findAll().stream().map(MarkerDto::toDto).toList();
    }
}
