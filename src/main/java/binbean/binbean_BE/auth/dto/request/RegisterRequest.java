package binbean.binbean_BE.auth.dto.request;

import binbean.binbean_BE.constants.Role;
import binbean.binbean_BE.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.crypto.password.PasswordEncoder;

public record RegisterRequest(
    @NotEmpty @Email
    String email,
    @NotEmpty
    String password,
    @NotEmpty
    String nickname,
    String profile,
    @NotEmpty
    Role role
) {

    public User toEntity() {
        return User.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .profile(profile)
            .role(role)
            .build();
    }
}
