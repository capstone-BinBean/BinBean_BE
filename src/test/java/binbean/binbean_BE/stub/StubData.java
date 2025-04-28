package binbean.binbean_BE.stub;

import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.dto.auth.request.LoginRequest;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.enums.user.Role;

public class StubData {
    public static class MockAuth {

        public static RegisterRequest getRegisterRequest() {
            return new RegisterRequest(
                "test@email.com",
                "password123",
                "testNickName",
                "",
                Role.ROLE_USER
            );
        }

        public static LoginRequest getLoginRequest() {
            return new LoginRequest(
                "test@email.com",
                "password123"
            );
        }

        public static TokenDto getTokenDto() {
            return TokenDto.builder()
                .grantType("Bearer")
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .authType("Authorization")
                .build();
        }
    }

}
