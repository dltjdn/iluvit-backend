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
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),

    /**
     * 404 BAD REQUEST
     */
    ALREADY_LOGIN_ID_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    ALREADY_NICKNAME_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}