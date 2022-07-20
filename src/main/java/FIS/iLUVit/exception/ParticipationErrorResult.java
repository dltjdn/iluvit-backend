package FIS.iLUVit.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ParticipationErrorResult {

    PARTICIPATION_NO_RESULTS(HttpStatus.BAD_REQUEST, "올바르지 않은 접근입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
