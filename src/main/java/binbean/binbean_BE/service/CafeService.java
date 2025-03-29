package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.request.CafeRegisterRequest;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest.FloorInfo;
import binbean.binbean_BE.dto.response.CafeInfoResponse;
import binbean.binbean_BE.entity.BusinessHours;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.CafeImg;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.repository.BusinessHoursRepository;
import binbean.binbean_BE.repository.CafeImgRepository;
import binbean.binbean_BE.repository.CafeRepository;
import binbean.binbean_BE.repository.floor_plan.BorderLineRepository;
import binbean.binbean_BE.repository.floor_plan.CounterRepository;
import binbean.binbean_BE.repository.floor_plan.DoorRepository;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import binbean.binbean_BE.repository.floor_plan.SeatsRepository;
import binbean.binbean_BE.repository.floor_plan.ToiletRepository;
import binbean.binbean_BE.repository.floor_plan.WindowRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class CafeService {

    private final CafeRepository cafeRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final FloorPlanRepository floorPlanRepository;
    private final BorderLineRepository borderLineRepository;
    private final SeatsRepository seatsRepository;
    private final DoorRepository doorRepository;
    private final CounterRepository counterRepository;
    private final ToiletRepository toiletRepository;
    private final WindowRepository windowRepository;
    private final ImageStorageService imageStorageService;
    private final CafeImgRepository cafeImgRepository;

    public CafeService(CafeRepository cafeRepository, BusinessHoursRepository businessHoursRepository,
        FloorPlanRepository floorPlanRepository, BorderLineRepository borderLineRepository,
        SeatsRepository seatsRepository, DoorRepository doorRepository, CounterRepository counterRepository,
        ToiletRepository toiletRepository, WindowRepository windowRepository,
        ImageStorageService imageStorageService, CafeImgRepository cafeImgRepository) {
        this.cafeRepository = cafeRepository;
        this.businessHoursRepository = businessHoursRepository;
        this.floorPlanRepository = floorPlanRepository;
        this.borderLineRepository = borderLineRepository;
        this.seatsRepository = seatsRepository;
        this.doorRepository = doorRepository;
        this.counterRepository = counterRepository;
        this.toiletRepository = toiletRepository;
        this.windowRepository = windowRepository;
        this.imageStorageService = imageStorageService;
        this.cafeImgRepository = cafeImgRepository;
    }

    public void registerCafe(CafeRegisterRequest cafeRequest, FloorPlanRegisterRequest floorRequest,
        List<MultipartFile> cafeImgFiles) {

        Cafe cafe = cafeRequest.toCafeEntity();
        cafeRepository.save(cafe);

        BusinessHours businessHours = cafeRequest.toBusinessHoursEntity();
        businessHoursRepository.save(businessHours);

        saveFloorPlan(floorRequest, cafe);

        saveCafeImages(cafe, cafeImgFiles);
    }

    public CafeInfoResponse getCafeInfo(){

    }

    private void saveFloorPlan(FloorPlanRegisterRequest floorRequest, Cafe cafe) {
        for (FloorPlanRegisterRequest.FloorInfo floorInfo : floorRequest.floorList()) {
            FloorPlan floorPlan = floorPlanRepository.save(floorInfo.toFloorPlanEntity(cafe));
            borderLineRepository.saveAll(floorInfo.toBorderLinesEntity(floorPlan));
            seatsRepository.saveAll(floorInfo.toSeatsEntities(floorPlan));
            doorRepository.saveAll(floorInfo.toDoorsEntity(floorPlan));
            counterRepository.saveAll(floorInfo.toCountersEntity(floorPlan));
            toiletRepository.saveAll(floorInfo.toToiletsEntity(floorPlan));
            windowRepository.saveAll(floorInfo.toWindowsEntity(floorPlan));
        }
    }

    private void saveCafeImages(Cafe cafe, List<MultipartFile> cafeImgFiles) {
        for (MultipartFile image : cafeImgFiles) {
            String imageUrl = imageStorageService.uploadImage(image);
            CafeImg cafeImg = CafeImg.toEntity(cafe, imageUrl);
            cafeImgRepository.save(cafeImg);
        }
    }
}
