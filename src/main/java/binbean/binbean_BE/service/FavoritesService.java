package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.request.FavoritesRequest;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.Favorites;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.FavoritesRepository;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final FloorPlanRepository floorPlanRepository;

    public FavoritesService(FavoritesRepository favoritesRepository, FloorPlanRepository floorPlanRepository) {
        this.favoritesRepository = favoritesRepository;
        this.floorPlanRepository = floorPlanRepository;
    }

    public void registerFavorites(FavoritesRequest request, User user) {
        FloorPlan floorPlan = floorPlanRepository.findById(request.floorPlanId())
            .orElseThrow(() -> new NotFoundException(String.format(ErrorMsg.CAFE_NOT_FOUND)));
        Cafe cafe = floorPlan.getCafe();
        List<Favorites> favoritesList = favoritesRepository.findByCafeIdAndUserId(cafe.getId(), user.getId());

        Favorites targetFavorite = findTargetFavorite(favoritesList, request.seatsId());

        if (targetFavorite  == null) {
            Favorites newFavorites = Favorites.create(user, cafe, request.seatsId());
            favoritesRepository.save(newFavorites);
        } else {
            targetFavorite.setIsFavorites(!targetFavorite.getIsFavorites());
            favoritesRepository.save(targetFavorite);
        }
    }

    private Favorites findTargetFavorite(List<Favorites> favoritesList, Long seatsId) {
        for (Favorites favorite : favoritesList) {
            if (seatsId.equals(favorite.getSeatsNumber())) {
                return favorite;
            }
        }

        return null;
    }
}
