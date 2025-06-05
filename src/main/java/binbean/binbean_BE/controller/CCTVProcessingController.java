package binbean.binbean_BE.controller;

import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.response.FloorPlanResponse;
import binbean.binbean_BE.service.SeatMappingService;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cctv")
public class CCTVProcessingController {

    private final SeatMappingService seatMappingService;

    public CCTVProcessingController(SeatMappingService seatMappingService) {
        this.seatMappingService = seatMappingService;
    }

    @PostMapping(value = "/detect", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<FloorPlanResponse> getDetectedItems(@RequestPart(value = "image") MultipartFile image,
        @RequestPart("floorList") FloorList floorList, @RequestPart("floorNumber") int floorNumber) throws IOException {
        var response = seatMappingService.getCurrentOccupiedSeats(image, floorList, floorNumber);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
