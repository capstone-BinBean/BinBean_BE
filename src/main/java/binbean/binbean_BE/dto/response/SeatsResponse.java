package binbean.binbean_BE.dto.response;

public record SeatsResponse(
    Long seatsNumber,
    int floorNumber,
    int seatsAvailable // FIXME: 객체 모델 검출 기능 이후 (0: 이용가능, 1: 이용중)
) {

}
