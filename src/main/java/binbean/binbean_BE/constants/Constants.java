package binbean.binbean_BE.constants;

import java.util.List;
import java.util.Set;

public class Constants {
    private Constants() {}

    public static class Kakao {
        public static final String KAKAO_OAUTH_QUERY_STRING =
            "/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code";
        public static final String KAKAO_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";
        public static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    }

    public static class URL {
        // SecurityConfig 허용 URL
        public static final String[] ALLOWED_URLS = {
            "/api/auths/registration",
            "/api/auths/login",
            "/api/auths/kakao/login"};
        // Verification Filter에서 토큰 검증을 하지 않는 URL
        public static final Set<String> EXCLUDED_URLS = Set.of("/signup", "/api/auths/kakao/login");

    }

    public static class User {
        public static final String ROLE_PREFIX = "ROLE_";
    }

    public static class LoggingMsg {
        public static final String ACCESS_TOKEN_MISSING = "Access token is missing in request: ";
    }

    public static class ErrorMsg {
        public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
        public static final String INVALID_CREDENTIALS = "이메일 또는 비밀번호가 올바르지 않습니다.";
        public static final String AUTHENTICATION_FAILED = "인증 실패";
    }
}
