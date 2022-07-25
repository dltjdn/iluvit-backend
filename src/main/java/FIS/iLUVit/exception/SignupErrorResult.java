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
    ALREADY_BELONG_CENTER(HttpStatus.BAD_REQUEST, "현재 속해있는 시설이 있습니다."),
    NOT_BELONG_CENTER(HttpStatus.BAD_REQUEST, "현재 속해있는 시설이 없습니다."),
    HAVE_TO_MANDATE(HttpStatus.BAD_REQUEST, "원장권한을 다른 교사에게 위임한 후 다시 요청해주세요."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
