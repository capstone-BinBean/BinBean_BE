package binbean.binbean_BE.repository.floor_plan;

import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorPlanRepository extends JpaRepository<FloorPlan, Long> {

}
