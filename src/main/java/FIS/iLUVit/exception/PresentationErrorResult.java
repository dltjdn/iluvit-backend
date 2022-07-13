package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PresentationErrorResult implements ErrorResult {

    PARTICIPATION_PERIOD_PASSED(HttpStatus.BAD_REQUEST, "설명회 신청기간이 종료되었습니다"),
    WRONG_PTDATE_ID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 ptDateId 입니다"),
    ALREADY_PARTICIPATED_IN(HttpStatus.BAD_REQUEST, "이미 설명회를 신청하셨습니다"),
    PRESENTATION_OVERCAPACITY(HttpStatus.BAD_REQUEST, "설명회 수용가능 인원이 초과되었습니다")
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
