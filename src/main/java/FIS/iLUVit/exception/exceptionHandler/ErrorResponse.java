package FIS.iLUVit.exception.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private HttpStatus status;
    private String error;

    public ErrorResponse(String message, HttpStatus status) {
        this.status = status;
        this.error = message;
    }
}
