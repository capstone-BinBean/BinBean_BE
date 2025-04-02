package binbean.binbean_BE.service;

import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.exception.AccessDeniedException;
import binbean.binbean_BE.exception.user.UserNotFoundException;
import binbean.binbean_BE.repository.UserRepository;
import java.awt.Image;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    public UserService(UserRepository userRepository, ImageStorageService imageStorageService) {
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    public void uploadProfileImage(Long userId, MultipartFile image, User currentUser) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(currentUser.getEmail()));

        // 현재 로그인한 사용자가 해당 userId와 일치하는지 확인
        if (!user.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException();
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
}
