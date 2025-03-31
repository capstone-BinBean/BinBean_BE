package binbean.binbean_BE.service;

import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FloorPlanService {

    private final FloorPlanRepository floorPlanRepository;

    public FloorPlanService(FloorPlanRepository floorPlanRepository) {
        this.floorPlanRepository = floorPlanRepository;
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
}
