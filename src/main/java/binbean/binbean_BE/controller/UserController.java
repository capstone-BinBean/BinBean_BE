package binbean.binbean_BE.controller;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService;}

    /**
     * 사용자 프로필 이미지가 존재하지 않을 경우
     */
    @PutMapping(value = "/{user_id}/profile-img", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> uploadProfileImage(@PathVariable(name = "user_id") Long userId,
        @RequestParam("image") MultipartFile image,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.uploadProfileImage(userId, image, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
