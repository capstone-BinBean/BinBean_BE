package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.BusinessHoursDto;
import binbean.binbean_BE.dto.request.CafeRegisterRequest;
import binbean.binbean_BE.dto.request.CafeUpdateRequest;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.dto.response.CafeInfoResponse;
import binbean.binbean_BE.dto.response.ReviewResponse;
import binbean.binbean_BE.entity.BusinessHours;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.CafeImg;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.BusinessHoursRepository;
import binbean.binbean_BE.repository.CafeImgRepository;
import binbean.binbean_BE.repository.CafeRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class CafeService {

    private final CafeRepository cafeRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final ImageStorageService imageStorageService;
    private final CafeImgRepository cafeImgRepository;
    private final ReviewService reviewService;
    private final FloorPlanService floorPlanService;
    private final BusinessHoursService businessHoursService;

    public CafeService(CafeRepository cafeRepository, BusinessHoursRepository businessHoursRepository,
        ImageStorageService imageStorageService, CafeImgRepository cafeImgRepository,
        ReviewService reviewService, FloorPlanService floorPlanService,
        BusinessHoursService businessHoursService) {
        this.cafeRepository = cafeRepository;
        this.businessHoursRepository = businessHoursRepository;
        this.imageStorageService = imageStorageService;
        this.cafeImgRepository = cafeImgRepository;
        this.reviewService = reviewService;
        this.floorPlanService = floorPlanService;
        this.businessHoursService = businessHoursService;
    }

    public void registerCafe(CafeRegisterRequest cafeRequest, FloorPlanRegisterRequest floorRequest,
        List<MultipartFile> cafeImgFiles, User user) {

        Cafe cafe = cafeRequest.toCafeEntity(user);
        cafeRepository.save(cafe);

        BusinessHours businessHours = cafeRequest.toBusinessHoursEntity();
        businessHoursRepository.save(businessHours);

        floorPlanService.saveFloorPlan(floorRequest, cafe);

        saveCafeImages(cafe, cafeImgFiles);
    }

    public CafeInfoResponse getCafeInfo(Long cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId)
            .orElseThrow(() -> new NotFoundException("Cafe not found with id: " + cafeId));

        BusinessHoursDto businessHoursDto = businessHoursService.getBusinessHoursForToday(cafe);
        double reviewAvg = reviewService.getReviewAvg(cafe);
        List<String> cafeImgUrl = getCafeImageUrls(cafe);
        List<ReviewResponse> reviewResponse = reviewService.getReview(cafe);
        List<Long> floorPlanId = floorPlanService.getFloorPlanIdByCafeId(cafe);

        return cafe.toCafeDto(businessHoursDto.getStartTime(), businessHoursDto.getEndTime(), reviewAvg,
            cafeImgUrl, reviewResponse, floorPlanId);
    }

    public void updateCafeInfo(CafeUpdateRequest request, List<MultipartFile> cafeImgFiles, User user) {

        Cafe cafe = cafeRepository.findByUser(user)
            .orElseThrow(() -> new NotFoundException("The user's cafe does not exist."));
        BusinessHours businessHours = businessHoursRepository.findByCafeId(cafe.getId())
            .orElseThrow(() -> new NotFoundException("There is no registered businessHorus."));

        cafe.update(request);
        businessHours.update(request);
        saveCafeImages(cafe, cafeImgFiles);
    }

    public void registerFavorites(Long cafeId, User user) {

    }

    private void saveCafeImages(Cafe cafe, List<MultipartFile> cafeImgFiles) {
        for (MultipartFile image : cafeImgFiles) {
            String imageUrl = imageStorageService.uploadImage(image);
            CafeImg cafeImg = CafeImg.toEntity(cafe, imageUrl);
            cafeImgRepository.save(cafeImg);
        }
    }

    private List<String> getCafeImageUrls(Cafe cafe) {
        List<CafeImg> cafeImg = cafeImgRepository.findByCafeId(cafe.getId());
        List<String> cafeImgUrls = new ArrayList<>();
        for (CafeImg img : cafeImg) {
            cafeImgUrls.add(img.getCafeImgUrl());
        }
        return cafeImgUrls;
    }
}
