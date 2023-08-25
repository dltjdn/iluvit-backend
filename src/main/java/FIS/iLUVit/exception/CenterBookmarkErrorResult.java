package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CenterBookmarkErrorResult implements ErrorResult {
    ALREADY_PREFER(HttpStatus.BAD_REQUEST, "이미 찜한 시설입니다."),
    NOT_VALID_CENTER(HttpStatus.I_AM_A_TEAPOT, "잘못된 시설 아이디 입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
