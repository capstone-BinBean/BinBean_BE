package binbean.binbean_BE.exception;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ClientErrorException {
    public UnauthorizedException() { super(HttpStatus.UNAUTHORIZED, ErrorMsg.REFRESH_EXPIRED); }

    public UnauthorizedException(HttpStatus status, String message) {
        super(status, message);
    }
}
