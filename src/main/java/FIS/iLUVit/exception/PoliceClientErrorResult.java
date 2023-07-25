package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PoliceClientErrorResult implements ErrorResult {

    NO_RESULT(HttpStatus.I_AM_A_TEAPOT, "올바르지 않은 접근입니다"),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "요청이 실패했습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다");

    private final HttpStatus httpStatus;
    private final String message;

}

