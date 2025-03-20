package binbean.binbean_BE.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String authType;
}
