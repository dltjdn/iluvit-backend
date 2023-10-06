package FIS.iLUVit.domain.authnum.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthNumberErrorResult implements ErrorResult {
    /**
     *  404 NOT FOUND
     */
    AUTHENTICATION_FAILED(HttpStatus.NOT_FOUND, "인증번호가 일치하지 않습니다."),
    PHONE_NUMBER_NOT_REGISTERED(HttpStatus.NOT_FOUND, "서비스에 가입되지 않은 핸드폰 번호입니다."),
    ID_OR_PASSWORD_MISMATCH(HttpStatus.NOT_FOUND, "아이디 또는 휴대폰번호가 일치하지 않습니다."),

    /**
     * 400 BAD REQUEST
     */
    PHONE_NUMBER_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 가입된 핸드폰 번호입니다."),
    AUTH_NUMBER_IN_PROGRESS(HttpStatus.BAD_REQUEST, "해당 번호로 인증 진행중입니다. 인증번호를 분실하였다면 3분 후 다시 시도해주세요"),
    AUTH_NUMBER_EXPIRED(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호확인이 서로 다릅니다."),
    PHONE_NUMBER_UNVERIFIED(HttpStatus.NOT_FOUND, "핸드폰 인증이 완료되지 않았습니다."),
    AUTH_KIND_MISMATCH(HttpStatus.BAD_REQUEST, "인증 종류가 일치하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;


}
