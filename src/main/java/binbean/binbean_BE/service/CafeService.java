package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.ImgUrl;
import binbean.binbean_BE.dto.ObjectId;
import binbean.binbean_BE.dto.OperatingHours;
import binbean.binbean_BE.dto.request.CafeRegisterRequest;
import binbean.binbean_BE.dto.request.CafeUpdateRequest;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.dto.response.CafeIdResponse;
import binbean.binbean_BE.dto.response.CafeInfoResponse;
import binbean.binbean_BE.dto.response.CafeSearchResponse;
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
import java.util.Collections;
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

    public List<CafeSearchResponse> searchCafe(String cafeName) {
        List<Cafe> cafes = cafeRepository.findByCafeNameContaining(cafeName);
        List<CafeSearchResponse> responses = new ArrayList<>();

        for (Cafe cafe : cafes) {
            responses.add(CafeSearchResponse.create(cafe.getId(), cafe.getCafeName(), cafe.getCafeAddress(),
                cafe.getCafeDescription()));
        }

        return responses;
    }

    public CafeIdResponse registerCafe(CafeRegisterRequest cafeRequest, List<FloorPlanRegisterRequest> floorRequest,
        List<MultipartFile> cafeImgFiles, User user) {
        Cafe cafe = cafeRequest.toCafeEntity(user);
        cafeRepository.save(cafe);

        BusinessHours businessHours = cafeRequest.toBusinessHoursEntity(cafe);
        businessHoursRepository.save(businessHours);

        floorPlanService.saveFloorPlan(floorRequest, cafe);

        if (cafeImgFiles == null) {
            cafeImgFiles = Collections.emptyList();
        }
        saveCafeImages(cafe, cafeImgFiles);

        CafeIdResponse response = CafeIdResponse.create(cafe.getId());

        return response;
    }

    public CafeInfoResponse getCafeInfo(Long cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId)
            .orElseThrow(() -> new NotFoundException(String.format(ErrorMsg.CAFE_NOT_FOUND)));

        OperatingHours operatingHours = businessHoursService.getBusinessHoursForToday(cafe);
        List<ImgUrl> cafeImgUrl = getCafeImageUrlList(cafe);
        double reviewAvg = reviewService.getReviewAvg(cafe);
        List<ReviewResponse> reviewResponse = reviewService.getReviewByCafe(cafe);
        List<ObjectId> floorPlanId = floorPlanService.getFloorPlanIdByCafe(cafe);

        return cafe.toCafeDto(operatingHours.startTime(), operatingHours.endTime(), cafeImgUrl, reviewAvg,
            reviewResponse, floorPlanId);
    }

    public void updateCafeInfo(CafeUpdateRequest request, List<MultipartFile> cafeImgFiles, User user) {

        Cafe cafe = cafeRepository.findByUser(user)
            .orElseThrow(() -> new NotFoundException("The user's cafe does not exist.")); // -> 403
        BusinessHours businessHours = businessHoursRepository.findByCafeId(cafe.getId())
            .orElseThrow(() -> new NotFoundException("There is no registered businessHorus."));

        cafe.update(request);
        businessHours.update(request);
        saveCafeImages(cafe, cafeImgFiles);
    }

    public List<Cafe> getAllCafe() {
        return cafeRepository.findAll();
    }

    private void saveCafeImages(Cafe cafe, List<MultipartFile> cafeImgFiles) {
        if (cafeImgFiles==null || cafeImgFiles.isEmpty()) {
            return;
        }
        for (MultipartFile image : cafeImgFiles) {
            String imageUrl = imageStorageService.uploadImage(image);
            CafeImg cafeImg = CafeImg.create(cafe, imageUrl);
            cafeImgRepository.save(cafeImg);
        }
    }

    private List<ImgUrl> getCafeImageUrlList(Cafe cafe) {
        List<CafeImg> cafeImg = cafeImgRepository.findByCafeId(cafe.getId());
        List<ImgUrl> cafeImgUrls = new ArrayList<>();
        for (CafeImg img : cafeImg) {
            cafeImgUrls.add(img.toImgUrlDto());
        }
        return cafeImgUrls;
    }
}
