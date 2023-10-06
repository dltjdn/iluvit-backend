package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ParticipationErrorResult implements ErrorResult {

    /**
     * 404 NOT FOUND
     */
    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 설명회 참여 정보가 존재하지 않습니다."),

    /**
     * 400 BAD REQUEST
     */
    WRONG_PARTICIPATION_ID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 설명회 참여 id 입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
