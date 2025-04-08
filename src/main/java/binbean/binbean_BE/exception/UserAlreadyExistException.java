package binbean.binbean_BE.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistException extends ClientErrorException {
    public UserAlreadyExistException() { super(HttpStatus.CONFLICT, "USER ALREADY EXISTS"); }

    public UserAlreadyExistException(String str) {
        super(HttpStatus.CONFLICT, "USER : " + str + " already exists");
    }
}
