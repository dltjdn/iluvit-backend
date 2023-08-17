package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BlockedErrorResult implements ErrorResult {
    ALREADY_BLOCKED_EXIST(HttpStatus.BAD_REQUEST, "이미 차단한 유저입니다."),
    IS_SAME_USER(HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수는 없습니다."),
    NOT_EXIST_BLOCKED(HttpStatus.NOT_FOUND, "존재하지 않는 차단관계입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
