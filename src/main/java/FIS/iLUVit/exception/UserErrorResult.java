package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 유저가 존재 하지 않습니다."),

    /**
     * 403 FORBIDDEN
     */
    NOT_VALID_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 토큰으로 사용자 접근입니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 요청에 대한 권한이 없습니다."),

    /**
     * 404 BAD REQUEST
     */
    ALREADY_LOGINID_EXIST(HttpStatus.BAD_REQUEST, "이미 존재 하는 아이디입니다."),
    ALREADY_NICKNAME_EXIST(HttpStatus.BAD_REQUEST, "이미 존재 하는 닉네임입니다."),
    NOT_VALID_REQUEST(HttpStatus.I_AM_A_TEAPOT, "잘못된 요청입니다."),

  ;

    private final HttpStatus httpStatus;
    private final String message;
}