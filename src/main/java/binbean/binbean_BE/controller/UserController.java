package binbean.binbean_BE.controller;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.request.ChangePasswordRequest;
import binbean.binbean_BE.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PutMapping(value = "/profile-img", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> uploadProfileImage(@RequestParam("profileImgUrl") MultipartFile profileImgUrl,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (profileImgUrl.isEmpty()) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); }

        userService.uploadProfileImage(profileImgUrl, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // FIXME : 비밀번호 변경 완료 이후, 로그아웃 처리 시킬지 확인 필요
        userService.changePassword(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
