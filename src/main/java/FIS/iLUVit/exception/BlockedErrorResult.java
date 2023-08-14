package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.SimpleTimeZone;

@Getter
@RequiredArgsConstructor
public enum BlockedErrorResult implements ErrorResult {
    ALREADY_BLOCKED_EXIST(HttpStatus.BAD_REQUEST, "이미 차단한 유저입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
