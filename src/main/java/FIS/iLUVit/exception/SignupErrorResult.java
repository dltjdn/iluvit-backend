package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SignupErrorResult implements ErrorResult {
    NOT_MATCH_PWDCHECK(HttpStatus.BAD_REQUEST, "비밀번호확인이 다릅니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    NOT_EXIST_CENTER(HttpStatus.BAD_REQUEST, "잘못된 시설로의 접근입니다."),
    NOT_MATCH_PWD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
