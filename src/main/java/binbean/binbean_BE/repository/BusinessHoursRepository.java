package binbean.binbean_BE.repository;

import binbean.binbean_BE.entity.BusinessHours;
import binbean.binbean_BE.entity.Cafe;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {

    Optional<BusinessHours> findByCafe(Cafe cafe);
}
