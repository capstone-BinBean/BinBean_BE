package binbean.binbean_BE.controller;

import binbean.binbean_BE.dto.aws.DetectedItem;
import binbean.binbean_BE.service.RekognitionService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/aws")
public class AwsRekognitionRestController {

    @Autowired
    RekognitionService rekognitionService;

    @PostMapping(value = "/test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<DetectedItem>> detectFaces(@RequestPart(value = "image") MultipartFile image) throws IOException {
        var response = rekognitionService.detectLabels(image);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
