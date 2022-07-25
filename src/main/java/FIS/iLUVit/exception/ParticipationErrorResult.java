package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ParticipationErrorResult implements ErrorResult {

    WRONG_PARTICIPATIONID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 접근입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
