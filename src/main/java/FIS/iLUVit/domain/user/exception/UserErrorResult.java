package FIS.iLUVit.domain.user.exception;

import FIS.iLUVit.global.exception.ErrorResult;
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
    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    PASSWORD_CHECK_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호 확인이 다릅니다."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    ALREADY_BELONGS_TO_CENTER(HttpStatus.BAD_REQUEST, "이미 속해있는 시설이 있습니다."),
    HAVE_TO_MANDATE(HttpStatus.BAD_REQUEST, "원장권한을 다른 교사에게 위임한 후 다시 요청해주세요."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}