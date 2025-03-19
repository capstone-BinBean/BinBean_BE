package binbean.binbean_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleClientErrorException(ClientErrorException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getStatus(), e.getMessage()), e.getStatus());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
    /**
    * 서버 내부 발생 에러 핸들링
    * 내부 에러 전달하는 대신 스테이터스 코드만 보내주는 걸로 간소화
    */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleClientErrorException(RuntimeException e) {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleClientErrorException(Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}
