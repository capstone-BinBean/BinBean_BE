package binbean.binbean_BE.exception;


import binbean.binbean_BE.constants.Constants;
import binbean.binbean_BE.constants.Constants.ErrorMsg;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ClientErrorException {
    public AccessDeniedException() {
        super(HttpStatus.UNAUTHORIZED, ErrorMsg.ACCESS_DENIED);
    }
}