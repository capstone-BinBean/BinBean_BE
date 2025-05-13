package binbean.binbean_BE.controller;

import binbean.binbean_BE.dto.DetectedItem;
import binbean.binbean_BE.service.RekognitionService;
import java.io.IOException;
import java.util.List;
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

    private final RekognitionService rekognitionService;

    public CCTVProcessingController(RekognitionService rekognitionService) {
        this.rekognitionService = rekognitionService;
    }

    @PostMapping(value = "/detect", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<DetectedItem>> getDetectedItems(@RequestPart(value = "image") MultipartFile image) throws IOException {
        var response = rekognitionService.getDetectedItems(image);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
