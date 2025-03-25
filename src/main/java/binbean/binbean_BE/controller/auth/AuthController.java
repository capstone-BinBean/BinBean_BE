package binbean.binbean_BE.controller.auth;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.auth.request.LoginRequest;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.dto.auth.request.SocialLoginRequest;
import binbean.binbean_BE.entity.user.User;
import binbean.binbean_BE.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auths")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public void login(@Valid @RequestBody LoginRequest request) {
    }

    @PostMapping("/kakao/login")
    public void socialLogin(@Valid @RequestBody SocialLoginRequest request) {
    }
}
