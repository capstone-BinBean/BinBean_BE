package binbean.binbean_BE.constants;

public class Constants {
    private Constants() {}

    public static class Kakao {
        public static final String KAKAO_OAUTH_QUERY_STRING =
            "/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code";
        public static final String KAKAO_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";
        public static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    }

    public static class URL {
        // SecurityConfig 허용하는 URL
        public static final String[] ALLOWED_URLS = {
            URL.REGISTER_URL,
            URL.NORMAL_LOGIN_URL,
            URL.KAKAO_LOGIN_URL,
            URL.REISSUE_URL};
        public static final String REGISTER_URL = "/api/auths/registration";
        public static final String KAKAO_LOGIN_URL = "/api/auths/kakao/login";
        public static final String NORMAL_LOGIN_URL = "/api/auths/login";
        public static final String REISSUE_URL = "/api/auths/reissue";
    }

    public static class User {
        public static final String ROLE_PREFIX = "ROLE_";
    }

    public static class FixedValue {
        public static final String PERSON = "Person";
    }

    public static class LoggingMsg {
        public static final String ACCESS_TOKEN_MISSING = "Access token is missing in request: ";
        public static final String LOGOUT_FLAG = "logout";
    }

    public static class ErrorMsg {
        public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
        public static final String USER_NOT_FOUND_WITH_EMAIL = "사용자를 찾을 수 없습니다. (email: %s)";
        public static final String USER_NOT_FOUND_WITH_ID = "사용자를 찾을 수 없습니다. (id: %s)";
        public static final String INVALID_CREDENTIALS = "이메일 또는 비밀번호가 올바르지 않습니다.";
        public static final String AUTHENTICATION_FAILED = "인증 실패";
        public static final String ACCESS_DENIED = "접근 권한이 없습니다.";
        public static final String PASSWORD_NOT_MATCH = "현재 비밀번호가 일치하지 않습니다.";
        public static final String PASSWORD_NOT_SERVICE = "소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.";
        public static final String JWT_SECRET_DECRYPT_ERROR = "JWT SECRET KEY 복호화 실패";
        public static final String AES_KEY_NOT_BASE64 = "복호화된 AES 키가 BASE64 형식이 아닙니다";
        public static final String MASTER_KEY_DECRYPT_ERROR = "AES 키 복호화 실패";
        public static final String AES_KEY_ENCRYPT_ERROR = "AES 기반 암호화 실패";
        public static final String AES_KEY_DECRYPT_ERROR = "AES 기반 복호화 실패";
        public static final String FAVORITE_NOT_FOUND_WITH_USER_AND_CAFE = "즐겨찾기를 찾을 수 없습니다.";
        public static final String CAFE_NOT_FOUND = "해당 카페를 찾을 수 없습니다.";
        public static final String LOGIN_EXPIRED = "로그인이 만료되었습니다. 다시 로그인해주세요.";
        public static final String REFRESH_EXPIRED = "리프레시 토큰이 유효하지 않습니다.";
    }
}
