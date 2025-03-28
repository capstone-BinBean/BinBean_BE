package binbean.binbean_BE.controller;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.marker.MarkerDto;
import binbean.binbean_BE.service.MarkerService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/markers")
public class MarkerController {

    private final MarkerService markerService;

    public MarkerController(MarkerService markerService) { this.markerService = markerService; }

    @GetMapping
    public ResponseEntity<List<MarkerDto>> register(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        var response = markerService.getMarkers(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
