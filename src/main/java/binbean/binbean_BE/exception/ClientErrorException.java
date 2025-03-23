package binbean.binbean_BE.exception;

import org.springframework.http.HttpStatus;

public class ClientErrorException extends RuntimeException {

    private final HttpStatus status;

    public ClientErrorException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return this.status; }
}
