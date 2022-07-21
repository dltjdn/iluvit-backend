package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult implements ErrorResult {

    NOT_VALID_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 토큰으로 사용자 접근입니디."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    NOT_AUTHORIZED_USER(HttpStatus.FORBIDDEN, "인증된 사용자가 아닙니다");

    private final HttpStatus httpStatus;
    private final String message;
}
