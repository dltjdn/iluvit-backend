package FIS.iLUVit.exception.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

public interface ErrorResult {
    HttpStatus getHttpStatus();
    String getMessage();
}
