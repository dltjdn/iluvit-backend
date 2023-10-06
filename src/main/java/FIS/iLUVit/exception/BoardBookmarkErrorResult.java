package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardBookmarkErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    BOARD_BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 북마크가 존재하지 않습니다."),

    /**
     * 403 FORBIDDEN
     */
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
