package binbean.binbean_BE.exception;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import org.springframework.http.HttpStatus;

public class ResponseStatusException extends ClientErrorException {
    public ResponseStatusException() {
        super(HttpStatus.BAD_REQUEST, ErrorMsg.ACCESS_DENIED);
    }

    public ResponseStatusException(HttpStatus status, String message) {
        super(status, message);
    }
}