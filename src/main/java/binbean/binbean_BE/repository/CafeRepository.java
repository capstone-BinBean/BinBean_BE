package binbean.binbean_BE.repository;

import binbean.binbean_BE.entity.Cafe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {

    /**
     * NOTE: 하버사인 공식을 활용한 JPQL을 고려했으나 네이티브 쿼리가 더 빠르고 최적화 가능하다는 장점으로 인해
     * 네이티브 쿼리로 사용
     */
    @Query(value = """
        SELECT * FROM CAFE_TB 
        WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) 
        * cos(radians(longitude) - radians(:longitude)) 
        + sin(radians(:latitude)) * sin(radians(latitude)))) <= :radius
        """, nativeQuery = true)

    List<Cafe> findCafesWithinRadius(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radius") double radius);
}
