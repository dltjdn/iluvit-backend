package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PresentationErrorResult implements ErrorResult {
    PRESENTATION_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 설명회입니다."),
    PTDATE_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 설명회 회차입니다."),

    PARTICIPATION_PERIOD_PASSED(HttpStatus.BAD_REQUEST, "설명회 신청기간이 종료되었습니다."),
    WRONG_PTDATE_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 설명회 회차입니다."),
    ALREADY_PARTICIPATED_IN(HttpStatus.BAD_REQUEST, "이미 설명회를 신청하셨습니다."),
    ALREADY_WAITED_IN(HttpStatus.BAD_REQUEST, "이미 설명회 대기를 하셨습니다."),
    PRESENTATION_NOT_OVERCAPACITY(HttpStatus.BAD_REQUEST, "아직 설명회 신청이 가득 차지 않아 대기 요청 할 수 없습니다."),
    PRESENTATION_OVERCAPACITY(HttpStatus.BAD_REQUEST, "설명회 수용 가능 인원이 초과되었습니다."),
    PRESENTATION_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 유효한 설명회가 존재합니다."),
    DELETE_FAIL_HAS_PARTICIPANT(HttpStatus.BAD_REQUEST, "설명회를 신청한 사용자가 있어 설명회 회차를 삭제할 수 없습니다."),
    UPDATE_ERROR_ABLE_PERSON_NUM(HttpStatus.BAD_REQUEST, "수용가능 인원을 현재 신청한 사용자 수보다 적게 설정할 수 없습니다."),

    CHECK_START_AND_END_DATE(HttpStatus.BAD_REQUEST, "시작일자와 종료일자를 다시 확인해 주세요.");


    ;

    private final HttpStatus httpStatus;
    private final String message;

}
