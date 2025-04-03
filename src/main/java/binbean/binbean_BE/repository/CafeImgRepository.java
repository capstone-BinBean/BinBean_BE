package binbean.binbean_BE.repository;

import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.CafeImg;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeImgRepository extends JpaRepository<CafeImg, Long> {

    List<CafeImg> findByCafe(Cafe cafe);
}
