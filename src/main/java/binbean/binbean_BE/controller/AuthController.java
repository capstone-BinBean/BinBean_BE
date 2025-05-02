package binbean.binbean_BE.controller;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.dto.auth.request.LoginRequest;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.dto.auth.request.SocialLoginRequest;
import binbean.binbean_BE.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // 헤더에서 액세스 토큰/리프레시 토큰 추출 (logout 요청 시 클라이언트에서 추가 헤더 정의하여 리프레시 토큰 전송)
        String accessToken = jwtTokenProvider.getHeaderAccessToken(request);
        String refreshToken = request.getHeader("X-Refresh-Token");
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/kakao/login")
    public void socialLogin(@Valid @RequestBody SocialLoginRequest request) {
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(HttpServletRequest request) {
        /**
         * 추후 안드로이드와의 논의 사항
         * => reissue 요청 시 클라이언트에서 가지고 있는 리프레시 토큰 전송을 헤더를 통해서 할 지
         * 아니면 별도의 RequestBody를 통해서 할 지 의논 필요
         */
        // 헤더에서 리프레시 토큰 추출 (reissue 요청 시 클라이언트에서 추가 헤더 정의하여 리프레시 토큰 전송)
        String refreshToken = request.getHeader("X-Refresh-Token");
        TokenDto token = authService.reissue(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
