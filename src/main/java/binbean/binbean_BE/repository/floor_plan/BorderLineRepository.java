package binbean.binbean_BE.repository.floor_plan;

import binbean.binbean_BE.entity.floor_plan.BorderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorderLineRepository extends JpaRepository<BorderLine, Long> {

}
