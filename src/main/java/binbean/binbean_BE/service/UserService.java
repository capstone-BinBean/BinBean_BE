package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.request.ChangePasswordRequest;
import binbean.binbean_BE.dto.response.FavoritesResponse;
import binbean.binbean_BE.dto.response.SeatsResponse;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.entity.floor_plan.Seats;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.exception.ResponseStatusException;
import binbean.binbean_BE.repository.FavoritesRepository;
import binbean.binbean_BE.repository.UserRepository;
import binbean.binbean_BE.repository.floor_plan.FloorPlanRepository;
import binbean.binbean_BE.repository.floor_plan.SeatsRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FavoritesRepository favoritesRepository;
    private final SeatsRepository seatsRepository;
    private final ImageStorageService imageStorageService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, FavoritesRepository favoritesRepository,
         SeatsRepository seatsRepository, ImageStorageService imageStorageService,
        BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.favoritesRepository = favoritesRepository;
        this.seatsRepository = seatsRepository;
        this.imageStorageService = imageStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    public void uploadProfileImage(MultipartFile image, User currentUser) {
        // 현재 로그인한 사용자와 DB에 등록된 사용자가 같은지 확인
        var user = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new NotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_WITH_EMAIL, currentUser.getEmail())));

        // 기존 프로필 이미지 삭제 (존재하는 경우)
        if (user.getProfile() != null) {
            imageStorageService.deleteImage(user.getProfile());
        }

        String imageUrl = imageStorageService.uploadImage(image);
        user.setProfile(imageUrl);
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request, User currentUser) {
        // 현재 로그인한 사용자와 DB에 등록된 사용자가 같은지 확인
        var user = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new NotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_WITH_EMAIL, currentUser.getEmail())));

        // 현재 비밀번호 검증
        if (request.currentPassword() != null) {
            // 소셜 로그인일 경우 비밀번호 변경 기능 제공 불가
            Optional.ofNullable(user.getPassword())
                .filter(password -> !password.isEmpty())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorMsg.PASSWORD_NOT_SERVICE));

            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMsg.PASSWORD_NOT_MATCH);
            }
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<FavoritesResponse> getFavoriteSeats(Long userId) {
        // 현재 로그인한 사용자와 DB에 등록된 사용자가 같은지 확인
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_WITH_ID, userId)));
        // 즐겨찾기 카페 목록
        var favorites = favoritesRepository.findByUserId(user.getId());

        return favorites.stream().map(favorite -> {
            var cafeId = favorite.getCafe().getId();

            // (즐겨찾기한) 카페들의 모든 좌석을 한번에 조회 (floorPlan, cafe까지 fetch join 됨)
            List<Seats> seats = seatsRepository.findByCafeIdWithFloorAndCafe(cafeId);
            List<SeatsResponse> seatsList = seats.stream()
                .map(seat -> new SeatsResponse(
                    seat.getId(),
                    seat.getFloorPlan().getFloorNumber(),
                    0))
                .toList();

            return new FavoritesResponse(cafeId, favorite.getCafe().getCafeName(), seatsList);
        }).toList();
    }
}
