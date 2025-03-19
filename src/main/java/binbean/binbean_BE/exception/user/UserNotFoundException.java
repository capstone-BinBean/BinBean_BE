package binbean.binbean_BE.exception.user;


import binbean.binbean_BE.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ClientErrorException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "USER NOT FOUND");
    }

    // 예외가 발생했을 때, 구체적인 이름를 알고 있을 경우
    public UserNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, "USER with " + email + " NOT FOUND");
    }

    public UserNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, "USER with user id " + userId + " NOT FOUND");
    }
}