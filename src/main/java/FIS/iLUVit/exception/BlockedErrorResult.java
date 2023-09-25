package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BlockedErrorResult implements ErrorResult {
    /**
     * 400 NOT FOUND
     */
    NOT_EXIST_BLOCKED(HttpStatus.NOT_FOUND, "차단 관계가 존재하지 않습니다."),

    /**
     * 404 BAD REQUEST
     */
    ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, "이미 차단한 유저입니다."),
    CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}