package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.dto.request.FloorPlanUpdateRequest;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.entity.floor_plan.BorderLine;
import binbean.binbean_BE.entity.floor_plan.Counter;
import binbean.binbean_BE.entity.floor_plan.Door;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.entity.floor_plan.Seats;
import binbean.binbean_BE.entity.floor_plan.Toilet;
import binbean.binbean_BE.entity.floor_plan.Window;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.CafeRepository;
import binbean.binbean_BE.repository.floor_plan.BorderLineRepository;
import binbean.binbean_BE.repository.floor_plan.CounterRepository;
import binbean.binbean_BE.repository.floor_plan.DoorRepository;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import binbean.binbean_BE.repository.floor_plan.SeatsRepository;
import binbean.binbean_BE.repository.floor_plan.ToiletRepository;
import binbean.binbean_BE.repository.floor_plan.WindowRepository;
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

    public FloorPlanService(CafeRepository cafeRepository, FloorPlanRepository floorPlanRepository,
        BorderLineRepository borderLineRepository, SeatsRepository seatsRepository,
        DoorRepository doorRepository, CounterRepository counterRepository, ToiletRepository toiletRepository,
        WindowRepository windowRepository, FloorPlanMapper floorPlanMapper) {
        this.cafeRepository = cafeRepository;
        this.floorPlanRepository = floorPlanRepository;
        this.borderLineRepository = borderLineRepository;
        this.seatsRepository = seatsRepository;
        this.doorRepository = doorRepository;
        this.counterRepository = counterRepository;
        this.toiletRepository = toiletRepository;
        this.windowRepository = windowRepository;
        this.floorPlanMapper = floorPlanMapper;
    }

    public List<Long> getFloorPlanIdByCafeId(Cafe cafe) {
        List<Long> floorPlanId = new ArrayList<>();
        List<FloorPlan> floorPlans = floorPlanRepository.findByCafe(cafe);

        if (floorPlans.isEmpty()) {
            throw new NotFoundException("Not found floorPlan");
        }

        for (FloorPlan floorPlan : floorPlans) {
            floorPlanId.add(floorPlan.getId());
        }

        return floorPlanId;
    }

    public void saveFloorPlan(FloorPlanRegisterRequest floorRequest, Cafe cafe) {
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

    @Transactional
    public void updateFloorPlan(List<FloorPlanUpdateRequest> requests, User user) {
        Cafe cafe = cafeRepository.findByUser(user)
            .orElseThrow(() -> new NotFoundException("The user's cafe does not exist."));
        List<FloorPlan> floorPlans = floorPlanRepository.findByCafe(cafe);
        Map<Integer, FloorPlan> floorPlanMap = floorPlans.stream()
            .collect(Collectors.toMap(FloorPlan::getFloorNumber, fp -> fp));

        for (FloorPlanUpdateRequest request : requests) {
            FloorPlan floorPlan = floorPlanMap.get(request.floorNumber());
            floorPlan.setMaxSeats(request.maxSeats());
            floorPlanRepository.save(floorPlan);

            List<BorderLine> borderLines = floorPlanMapper.toBorderLines(request.floorList(), floorPlan);
            borderLineRepository.saveAll(borderLines);

            List<Seats> seats = floorPlanMapper.toSeats(request.floorList(), floorPlan);
            seatsRepository.saveAll(seats);

            List<Door> doors = floorPlanMapper.toDoors(request.floorList(), floorPlan);
            doorRepository.saveAll(doors);

            List<Counter> counters = floorPlanMapper.toCounters(request.floorList(), floorPlan);
            counterRepository.saveAll(counters);

            List<Toilet> toilets = floorPlanMapper.toToilets(request.floorList(), floorPlan);
            toiletRepository.saveAll(toilets);

            List<Window> windows = floorPlanMapper.toWindows(request.floorList(), floorPlan);
            windowRepository.saveAll(windows);
        }
    }
}
