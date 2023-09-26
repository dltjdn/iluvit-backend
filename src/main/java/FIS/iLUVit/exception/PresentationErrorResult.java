package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PresentationErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    PRESENTATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 설명회가 존재하지 않습니다."),
    PTDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 설명회 회차가 존재하지 않습니다."),

    /**
     * 400 BAD REQUEST
     */
    PARTICIPATION_PERIOD_EXPIRED(HttpStatus.BAD_REQUEST, "설명회 신청기간이 종료되었습니다."),
    INVALID_PTDATE_ID(HttpStatus.BAD_REQUEST, "올바르지 않은 설명회 회차 id 입니다."),
    ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "이미 설명회를 신청하셨습니다."),
    ALREADY_ON_WAIT(HttpStatus.BAD_REQUEST, "이미 설명회 대기를 신청하셨습니다."),
    NOT_REACHED_CAPACITY_FOR_WAIT(HttpStatus.BAD_REQUEST, "아직 설명회 신청이 가득 차지 않아 대기 요청 할 수 없습니다."),
    CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "설명회 수용가능 인원이 초과되었습니다."),
    VALID_PRESENTATION_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 유효한 설명회가 존재합니다."),
    CANNOT_DELETE_WITH_PARTICIPANT(HttpStatus.BAD_REQUEST, "설명회를 신청한 사용자가 있어 설명회 회차를 삭제할 수 없습니다."),
    INSUFFICIENT_CAPACITY_SETTING(HttpStatus.BAD_REQUEST, "수용가능 인원을 현재 신청한 사용자 수보다 적게 설정할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
