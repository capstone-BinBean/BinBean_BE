package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.ObjectId;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.dto.request.FloorPlanUpdateRequest;
import binbean.binbean_BE.dto.response.FloorPlanResponse;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.CafeRepository;
import binbean.binbean_BE.repository.floor_plan.BorderLineRepository;
import binbean.binbean_BE.repository.floor_plan.CounterRepository;
import binbean.binbean_BE.repository.floor_plan.DoorRepository;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import binbean.binbean_BE.repository.floor_plan.SeatsRepository;
import binbean.binbean_BE.repository.floor_plan.ToiletRepository;
import binbean.binbean_BE.repository.floor_plan.WindowRepository;
import binbean.binbean_BE.util.FloorPlanResponseMapper;
import binbean.binbean_BE.util.FloorPlanMapper;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FloorPlanService {

    private final CafeRepository cafeRepository;
    private final FloorPlanRepository floorPlanRepository;
    private final BorderLineRepository borderLineRepository;
    private final SeatsRepository seatsRepository;
    private final DoorRepository doorRepository;
    private final CounterRepository counterRepository;
    private final ToiletRepository toiletRepository;
    private final WindowRepository windowRepository;
    private final FloorPlanMapper floorPlanMapper;
    private final FloorPlanResponseMapper floorPlanResponseMapper;

    public FloorPlanService(CafeRepository cafeRepository, FloorPlanRepository floorPlanRepository,
        BorderLineRepository borderLineRepository, SeatsRepository seatsRepository,
        DoorRepository doorRepository, CounterRepository counterRepository, ToiletRepository toiletRepository,
        WindowRepository windowRepository, FloorPlanMapper floorPlanMapper,
        FloorPlanResponseMapper floorPlanResponseMapper) {
        this.cafeRepository = cafeRepository;
        this.floorPlanRepository = floorPlanRepository;
        this.borderLineRepository = borderLineRepository;
        this.seatsRepository = seatsRepository;
        this.doorRepository = doorRepository;
        this.counterRepository = counterRepository;
        this.toiletRepository = toiletRepository;
        this.windowRepository = windowRepository;
        this.floorPlanMapper = floorPlanMapper;
        this.floorPlanResponseMapper = floorPlanResponseMapper;
    }

    public List<ObjectId> getFloorPlanIdByCafe(Cafe cafe) {
        List<ObjectId> floorPlanId = new ArrayList<>();
        List<FloorPlan> floorPlans = floorPlanRepository.findByCafeId(cafe.getId());

        if (floorPlans.isEmpty()) {
            throw new NotFoundException("Not found floorPlan");
        }

        for (FloorPlan floorPlan : floorPlans) {
            floorPlanId.add(floorPlan.toObjectIdDto());
        }

        return floorPlanId;
    }

    @Transactional
    public void saveFloorPlan(List<FloorPlanRegisterRequest> requests, Cafe cafe) {
        for (FloorPlanRegisterRequest request : requests) {
            FloorPlan floorPlan = FloorPlan.create(cafe, request.floorNumber(), request.maxSeats());
            floorPlanRepository.save(floorPlan);
            FloorList floorList = request.floorList();

            borderLineRepository.saveAll(floorPlanMapper.createBorderLines(floorList, floorPlan));
            seatsRepository.saveAll(floorPlanMapper.createSeats(floorList, floorPlan));
            doorRepository.saveAll(floorPlanMapper.createDoors(floorList, floorPlan));
            counterRepository.saveAll(floorPlanMapper.createCounters(floorList, floorPlan));
            toiletRepository.saveAll(floorPlanMapper.createToilets(floorList, floorPlan));
            windowRepository.saveAll(floorPlanMapper.createWindows(floorList, floorPlan));
        }
    }

    @Transactional
    public void updateFloorPlan(List<FloorPlanUpdateRequest> requests, User user) {
        Cafe cafe = cafeRepository.findByUser(user)
            .orElseThrow(() -> new NotFoundException("The user's cafe does not exist."));
        List<FloorPlan> floorPlans = floorPlanRepository.findByCafeId(cafe.getId());
        Map<Integer, FloorPlan> floorPlanMap = floorPlans.stream()
            .collect(Collectors.toMap(FloorPlan::getFloorNumber, fp -> fp));

        for (FloorPlanUpdateRequest request : requests) {
            FloorPlan floorPlan = floorPlanMap.get(request.floorNumber());
            floorPlan.setMaxSeats(request.maxSeats());
            floorPlanRepository.save(floorPlan);

            FloorList floorList = request.floorList();

            borderLineRepository.saveAll(floorPlanMapper.createBorderLines(floorList, floorPlan));
            seatsRepository.saveAll(floorPlanMapper.createSeats(floorList, floorPlan));
            doorRepository.saveAll(floorPlanMapper.createDoors(floorList, floorPlan));
            counterRepository.saveAll(floorPlanMapper.createCounters(floorList, floorPlan));
            toiletRepository.saveAll(floorPlanMapper.createToilets(floorList, floorPlan));
            windowRepository.saveAll(floorPlanMapper.createWindows(floorList, floorPlan));
        }
    }

    public List<FloorPlanResponse> getFloorPlan(Long cafeId) {
        List<FloorPlan> floorPlans = floorPlanRepository.findByCafeId(cafeId);

        return floorPlans.stream()
            .map(floorPlanResponseMapper::floorPlanResponse)
            .collect(Collectors.toList());
    }
}
