package FIS.iLUVit.domain.waiting.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum WaitingErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    WAITING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 대기 정보가 존재하지 않습니다."),

    /**
     * 400 BAD REQUEST
     */
    WRONG_WAITING_ID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 대기 Id 입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
