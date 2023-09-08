package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult implements ErrorResult {

    NOT_VALID_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 토큰으로 사용자 접근입니다."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 아이디를 가진 유저가 존재 하지 않습니다."),
    NOT_VALID_REQUEST(HttpStatus.I_AM_A_TEAPOT, "잘못된 요청입니다."),
    NOT_LOGIN(HttpStatus.FORBIDDEN, "인증된 사용자가 아닙니다."),
    HAVE_NOT_AUTHORIZATION(HttpStatus.FORBIDDEN, "해당 요청에 대한 권한이 없습니다."),
    ALREADY_LOGINID_EXIST(HttpStatus.BAD_REQUEST, "이미 존재 하는 아이디입니다."),
    ALREADY_NICKNAME_EXIST(HttpStatus.BAD_REQUEST, "이미 존재 하는 닉네임입니다."),
    USER_IS_BLACK(HttpStatus.EXPECTATION_FAILED, "차단된 유저입니다."),
    USER_IS_WITHDRAWN(HttpStatus.PRECONDITION_FAILED, "탈퇴 후 15일이 지나지 않은 유저입니다."),
    USER_IS_BLACK_OR_WITHDRAWN(HttpStatus.EXPECTATION_FAILED, "차단 됐거나 탈퇴 후 15일이 지나지 않은 유저입니다."),
    WRONG_ADDRESS(HttpStatus.BAD_REQUEST, "잘못된 주소입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}