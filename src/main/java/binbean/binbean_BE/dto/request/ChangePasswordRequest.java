package binbean.binbean_BE.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ChangePasswordRequest(
    @NotEmpty
    String currentPassword,
    @NotEmpty
    String newPassword
) {

}
