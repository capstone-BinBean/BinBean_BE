package binbean.binbean_BE.dto.auth.request;

import jakarta.validation.constraints.NotEmpty;

public record SocialRegisterRequest(
    @NotEmpty
    String accessToken
) {

}
