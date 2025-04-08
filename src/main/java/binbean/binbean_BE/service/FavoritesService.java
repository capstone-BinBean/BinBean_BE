package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.request.FavoritesRequest;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.Favorites;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.CafeRepository;
import binbean.binbean_BE.repository.FavoritesRepository;
import org.springframework.stereotype.Service;

@Service
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final CafeRepository cafeRepository;

    public FavoritesService(FavoritesRepository favoritesRepository, CafeRepository cafeRepository) {
        this.favoritesRepository = favoritesRepository;
        this.cafeRepository = cafeRepository;
    }

    public void registerFavorites(FavoritesRequest request, User user) {
        Cafe cafe = cafeRepository.findByFloorPlanId(request.floorPlanId())
            .orElseThrow(() -> new NotFoundException(String.format(ErrorMsg.CAFE_NOT_FOUND)));
        Favorites favorites = favoritesRepository.findByCafeIdAndUserId(cafe.getId(), user.getId());

        if (favorites == null) {
            Favorites newFavorites = Favorites.builder()
                .user(user)
                .cafe(cafe)
                .isFavorites(true)
                .build();

            favoritesRepository.save(newFavorites);
        } else {
            favorites.setFavorites(!favorites.getIsFavorites());
            favoritesRepository.save(favorites);
        }
    }
}
