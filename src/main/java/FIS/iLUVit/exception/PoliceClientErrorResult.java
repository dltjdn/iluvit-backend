package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PoliceClientErrorResult implements ErrorResult {

    /**
     * 408 REQUEST TIMEOUT
     */
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "요청이 실패했습니다."),
    ;


    private final HttpStatus httpStatus;
    private final String message;

}