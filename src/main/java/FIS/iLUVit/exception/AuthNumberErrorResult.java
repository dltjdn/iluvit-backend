package FIS.iLUVit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthNumberErrorResult {

    ALREADY_PHONENUMBER_REGISTER(HttpStatus.BAD_REQUEST, "이미 가입된 핸드폰 번호입니다."),
    YET_AUTHNUMBER_VALID(HttpStatus.BAD_REQUEST, "해당 번호로 인증 진행중입니다. 인증번호를 분실하였다면 3분 후 다시 시도해주세요");

    private final HttpStatus httpStatus;
    private final String message;
}
