package binbean.binbean_BE.auth.dto.request;

import binbean.binbean_BE.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
    @NotEmpty
    @Email
    String email,
    @NotEmpty
    String password
) {
}
