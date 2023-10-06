package FIS.iLUVit.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorResult {
    HttpStatus getHttpStatus();
    String getMessage();
}
