package binbean.binbean_BE.dto.auth.request;

import binbean.binbean_BE.enums.user.Role;
import binbean.binbean_BE.entity.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @NotEmpty @Email
    String email,
    String password,
    @NotEmpty
    String nickname,
    String profile,
    @NotNull
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
