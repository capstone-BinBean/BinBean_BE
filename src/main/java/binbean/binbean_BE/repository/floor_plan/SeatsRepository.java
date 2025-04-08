package binbean.binbean_BE.repository.floor_plan;

import binbean.binbean_BE.entity.floor_plan.Seats;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatsRepository extends JpaRepository<Seats, Long> {
    List<Seats> findByFloorPlanId(Long floorId);

    /**
     * cafeId로 좌석과 floorplan과 cafe까지 한 번에 조회함
     * Seats가 ManyToOne으로 FloorPlan을 가지고 있는 상황
     * (cafeId를 기준으로 필터링하여 한 카페에서 즐겨찾기한 전체 층과 좌석을 한 번에 조회)
     *
     * Seats 테이블에서 floorPlan을 통해 cafeId로 필터링. EntityGraph를 통해 연관된 엔티티를
     * fetch join처럼 한 번에 가져옴
     */
    @EntityGraph(attributePaths = {"floorPlan", "floorPlan.cafe"})
    @Query("SELECT s FROM Seats s WHERE s.floorPlan.cafe.id = :cafeId")
    List<Seats> findByCafeIdWithFloorAndCafe(@Param("cafeId") Long cafeId);
}
