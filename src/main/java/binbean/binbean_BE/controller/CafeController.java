package binbean.binbean_BE.controller;

import binbean.binbean_BE.dto.request.CafeRegisterRequest;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.service.CafeService;
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
@RequestMapping("/api/cafes")
public class CafeController {

    private final CafeService cafeService;

    public CafeController(CafeService cafeService){
        this.cafeService = cafeService;
    }

    @PostMapping(value = "/registration", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> registerCafe(@RequestPart("cafe") CafeRegisterRequest cafeRequest,
        @RequestPart("floorPlan") FloorPlanRegisterRequest floorRequest,
        @RequestPart(value = "cafeImg", required = false) List<MultipartFile> cafeImg) {

        cafeService.registerCafe(cafeRequest, floorRequest, cafeImg);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
