package binbean.binbean_BE.auth.controller;

import binbean.binbean_BE.auth.dto.request.LoginRequest;
import binbean.binbean_BE.auth.dto.request.RegisterRequest;
import binbean.binbean_BE.auth.service.AuthService;
import binbean.binbean_BE.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auths")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public void login(@Valid @RequestBody LoginRequest request) {
    }

    @PostMapping("/test")
    public ResponseEntity<User> test(@Valid @RequestBody LoginRequest request) {
        var response = authService.test(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
