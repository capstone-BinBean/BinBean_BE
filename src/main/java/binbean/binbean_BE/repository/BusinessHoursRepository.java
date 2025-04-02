package binbean.binbean_BE.repository;

import binbean.binbean_BE.entity.BusinessHours;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {

    Optional<BusinessHours> findByCafeId(Long cafeId);
}
