package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BlackUserResult implements ErrorResult {
    BLACK_USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 로그인아이디를 가진 블랙유저가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}