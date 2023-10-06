package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CenterBookmarkErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    CENTER_BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 시설 즐겨찾기입니다."),

    /**
     * 400 BAD REQUEST
     */
    ALREADY_CENTER_BOOKMARKED(HttpStatus.BAD_REQUEST, "이미 즐겨찾기 한 시설입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
