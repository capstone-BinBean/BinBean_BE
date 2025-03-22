package binbean.binbean_BE.repository.floor_plan;

import binbean.binbean_BE.entity.floor_plan.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToiletRepository extends JpaRepository<Toilet, Long> {

}
