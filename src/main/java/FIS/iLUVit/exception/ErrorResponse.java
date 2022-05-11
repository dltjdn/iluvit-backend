package FIS.iLUVit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private Integer status;
    private String error;

    public ErrorResponse(String message, int status) {
        this.status = status;
        this.error = message;
    }
}
