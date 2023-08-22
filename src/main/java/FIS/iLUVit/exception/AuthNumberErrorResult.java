package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthNumberErrorResult implements ErrorResult {

    ALREADY_PHONENUMBER_REGISTER(HttpStatus.BAD_REQUEST, "이미 가입된 핸드폰 번호입니다."),
    YET_AUTHNUMBER_VALID(HttpStatus.BAD_REQUEST, "해당 번호로 인증 진행중입니다. 인증번호를 분실하였다면 3분 후 다시 시도해주세요"),
    AUTHENTICATION_FAIL(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    EXPIRED(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다."),
    NOT_SIGNUP_PHONE(HttpStatus.BAD_REQUEST, "서비스에 가입되지 않은 핸드폰 번호입니다."),
    NOT_MATCH_INFO(HttpStatus.BAD_REQUEST, "아이디 또는 휴대폰번호를 확인해주세요."),
    NOT_MATCH_CHECKPWD(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호확인이 서로 다릅니다."),
    NOT_AUTHENTICATION(HttpStatus.BAD_REQUEST, "핸드폰 인증이 완료되지 않았습니다."),
    NOT_MATCH_AUTHKIND(HttpStatus.BAD_REQUEST, "인증 종류가 일치하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;


}
