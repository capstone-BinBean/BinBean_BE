package binbean.binbean_BE.controller;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.request.CafeRegisterRequest;
import binbean.binbean_BE.dto.request.CafeUpdateRequest;
import binbean.binbean_BE.dto.request.FavoritesRequest;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.dto.request.FloorPlanUpdateRequest;
import binbean.binbean_BE.dto.response.CafeInfoResponse;
import binbean.binbean_BE.dto.response.FloorPlanResponse;
import binbean.binbean_BE.service.CafeService;
import binbean.binbean_BE.service.FavoritesService;
import binbean.binbean_BE.service.FloorPlanService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cafes")
public class CafeController {

    private final CafeService cafeService;
    private final FloorPlanService floorPlanService;
    private final FavoritesService favoritesService;

    public CafeController(CafeService cafeService, FloorPlanService floorPlanService,
        FavoritesService favoritesService) {
        this.cafeService = cafeService;
        this.floorPlanService = floorPlanService;
        this.favoritesService = favoritesService;
    }

    @PostMapping(value = "/registration", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> registerCafe(@RequestPart("cafe") CafeRegisterRequest cafeRequest,
        @RequestPart("floorPlan") FloorPlanRegisterRequest floorRequest,
        @RequestPart(value = "cafeImg", required = false) List<MultipartFile> cafeImgFiles,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        cafeService.registerCafe(cafeRequest, floorRequest, cafeImgFiles, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{cafe_id}")
    public ResponseEntity<CafeInfoResponse> getCafeInfo(@PathVariable(name = "cafe_id") Long cafeId) {

        CafeInfoResponse response = cafeService.getCafeInfo(cafeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updateCafeInfo(@RequestPart("cafe") CafeUpdateRequest request,
        @RequestPart(value = "cafeImg", required = false) List<MultipartFile> cafeImgFiles,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        cafeService.updateCafeInfo(request, cafeImgFiles, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/floor-plan")
    public ResponseEntity<Void> updateFloorPlan(@RequestBody List<FloorPlanUpdateRequest> requests,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        floorPlanService.updateFloorPlan(requests, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/floor-plan/{cafe_id}")
    public ResponseEntity<List<FloorPlanResponse>> getFloorPlan(@PathVariable(name = "cafe_id") Long cafeId) {

        List<FloorPlanResponse> response = floorPlanService.getFloorPlan(cafeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/favorites")
    public ResponseEntity<Void> registerFavorites(@RequestBody FavoritesRequest request,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        favoritesService.registerFavorites(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
