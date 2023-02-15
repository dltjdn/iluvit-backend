package FIS.iLUVit.exception.exceptionHandler;

import org.springframework.http.HttpStatus;

public interface ErrorResult {
    HttpStatus getHttpStatus();
    String getMessage();
}
