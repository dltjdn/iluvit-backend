package FIS.iLUVit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthNumberErrorResult {

    ALREADY_PHONENUMBER_REGISTER(HttpStatus.BAD_REQUEST, "이미 가입된 핸드폰 번호입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
