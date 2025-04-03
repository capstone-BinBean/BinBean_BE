package binbean.binbean_BE.dto.response;

import binbean.binbean_BE.entity.Favorites;
import lombok.Builder;

@Builder
public record FavoritesResponse (
    Long cafeId,
    String cafeName,
    int floorNumber,
    int SeatsNumber,
    int seatsAvailable // FIXME: 객체 모델 검출 기능 이후
){
//    public static FavoritesResponse toDto(Favorites favorites) {
//        return FavoritesResponse.builder()
//            .cafeId(favorites.getCafe().getId())
//            .cafeName(favorites.getCafe().getCafeName())
////            .floorNumber(favorites.getCafe().)
//    }

}
