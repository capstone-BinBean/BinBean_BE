package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.floor_plan.BorderLineRepository;
import binbean.binbean_BE.repository.floor_plan.CounterRepository;
import binbean.binbean_BE.repository.floor_plan.DoorRepository;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import binbean.binbean_BE.repository.floor_plan.SeatsRepository;
import binbean.binbean_BE.repository.floor_plan.ToiletRepository;
import binbean.binbean_BE.repository.floor_plan.WindowRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FloorPlanService {

    private final FloorPlanRepository floorPlanRepository;
    private final BorderLineRepository borderLineRepository;
    private final SeatsRepository seatsRepository;
    private final DoorRepository doorRepository;
    private final CounterRepository counterRepository;
    private final ToiletRepository toiletRepository;
    private final WindowRepository windowRepository;

    public FloorPlanService(FloorPlanRepository floorPlanRepository,
        BorderLineRepository borderLineRepository, SeatsRepository seatsRepository,
        DoorRepository doorRepository, CounterRepository counterRepository, ToiletRepository toiletRepository,
        WindowRepository windowRepository) {
        this.floorPlanRepository = floorPlanRepository;
        this.borderLineRepository = borderLineRepository;
        this.seatsRepository = seatsRepository;
        this.doorRepository = doorRepository;
        this.counterRepository = counterRepository;
        this.toiletRepository = toiletRepository;
        this.windowRepository = windowRepository;
    }

    public List<Long> getFloorPlanIdByCafeId(Long cafeId) {
        List<Long> floorPlanId = new ArrayList<>();
        List<FloorPlan> floorPlans = floorPlanRepository.findByCafeId(cafeId);

        if (floorPlans.isEmpty()) {
            throw new NotFoundException("Not found floorPlan");
        }

        for (FloorPlan floorPlan : floorPlans) {
            floorPlanId.add(floorPlan.getId());
        }

        return floorPlanId;
    }

    public FloorPlan getFloorPlanByCafe(Cafe cafe) {
        return floorPlanRepository.findByCafe(cafe);
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
}
