package binbean.binbean_BE.dto.response;

import binbean.binbean_BE.entity.Favorites;
import binbean.binbean_BE.repository.FavoritesRepository;
import java.util.List;
import lombok.Builder;

@Builder
public record FavoritesResponse (
    Long cafeId,
    String cafeName,
    List<SeatsResponse> seatsList
){

    public static FavoritesResponse create(Favorites favorites, List<SeatsResponse> seatsList) {
        return FavoritesResponse.builder()
            .cafeId(favorites.getCafe().getId())
            .cafeName(favorites.getCafe().getCafeName())
            .seatsList(seatsList)
            .build();
    }
}
