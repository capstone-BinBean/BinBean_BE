package binbean.binbean_BE.constants;

public class Constants {
    private Constants() {}

    public static class User {
        public static final String ROLE_PREFIX = "ROLE_";
    }

    public static class ErrorMsg {
        public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
        public static final String INVALID_CREDENTIALS = "이메일 또는 비밀번호가 올바르지 않습니다.";
        public static final String AUTHENTICATION_FAILED = "인증 실패";
    }
}
