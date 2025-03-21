package binbean.binbean_BE.repository.floor_plan;

import binbean.binbean_BE.entity.floor_plan.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {

}
