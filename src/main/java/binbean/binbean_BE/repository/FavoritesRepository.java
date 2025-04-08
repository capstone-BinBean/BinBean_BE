package binbean.binbean_BE.repository;

import binbean.binbean_BE.entity.Favorites;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    List<Favorites> findByUserId(Long userId);
    List<Favorites> findByCafeIdAndUserId(Long cafeId, Long userId);
}