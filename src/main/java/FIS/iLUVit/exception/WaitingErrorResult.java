package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum WaitingErrorResult implements ErrorResult {

    WRONG_WAITINGID_REQUEST(HttpStatus.I_AM_A_TEAPOT, "올바르지 않은 waitingId 입니다"),
    NO_RESULT(HttpStatus.I_AM_A_TEAPOT, "잘못된 요청입니다");
    private final HttpStatus httpStatus;
    private final String message;

}
