package FIS.iLUVit.exception.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status;
    private String error;

    public ErrorResponse(ErrorResult errorResult) {
        this.status = errorResult.getHttpStatus();
        this.error = errorResult.getMessage();
    }
}
