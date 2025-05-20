package binbean.binbean_BE.controller;

import binbean.binbean_BE.dto.marker.CafeMarkerResponse;
import binbean.binbean_BE.service.MarkerService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/markers")
public class MarkerController {

    private final MarkerService markerService;

    public MarkerController(MarkerService markerService) { this.markerService = markerService; }

    @GetMapping
    public ResponseEntity<List<CafeMarkerResponse>> getMarkers(@RequestParam(name = "latitude") Double latitude,
        @RequestParam(name = "longitude") Double longitude) {
        var response = markerService.getMarkers(latitude, longitude);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
