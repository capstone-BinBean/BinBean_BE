package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.request.ChangePasswordRequest;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.exception.ResponseStatusException;
import binbean.binbean_BE.exception.user.UserNotFoundException;
import binbean.binbean_BE.repository.UserRepository;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ImageStorageService imageStorageService,
        BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    public void uploadProfileImage(Long userId, MultipartFile image, User currentUser) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(currentUser.getEmail()));

        // 현재 로그인한 사용자와 DB에 등록된 사용자가 같은지 확인
        if (!user.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorMsg.ACCESS_DENIED);
        }

        // 기존 프로필 이미지 삭제 (존재하는 경우)
        if (user.getProfile() != null) {
            imageStorageService.deleteImage(user.getProfile());
        }

        // FIXME : imageStorageService의 uploadFile 메서드 분리 (버킷명 분리 필요)
        String imageUrl = imageStorageService.uploadImage(image);
        user.setProfile(imageUrl);
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request, User currentUser) {
        var user = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new UserNotFoundException(currentUser.getEmail()));

        // 현재 로그인한 사용자와 DB에 등록된 사용자가 같은지 확인
        if (!user.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorMsg.ACCESS_DENIED);
        }

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
}
