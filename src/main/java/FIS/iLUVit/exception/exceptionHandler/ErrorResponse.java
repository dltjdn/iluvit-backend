package FIS.iLUVit.exception.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private HttpStatus status;
    private String error;


    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorResult errorResult) {
        return ResponseEntity
                .status(errorResult.getHttpStatus())
                .body(
                        ErrorResponse.builder()
                                .status(errorResult.getHttpStatus())
                                .error(errorResult.getMessage())
                                .build()
                );

    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpStatus httpStatus, String message) {
        return ResponseEntity
                .status(httpStatus)
                .body(
                        ErrorResponse.builder()
                                .status(httpStatus)
                                .error(message)
                                .build()
                );
    }
}
