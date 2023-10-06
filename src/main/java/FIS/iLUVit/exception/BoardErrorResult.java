package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 게시판이 존재하지 않습니다"),

    /**
     * 403 FORBIDDEN
     */
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),

    /**
     * 400 BAD REQUEST
     */
    DUPLICATE_BOARD_NAME(HttpStatus.BAD_REQUEST, "해당 이름의 게시판이 이미 존재합니다."),
    CANNOT_DELETE_DEFAULT_BOARD(HttpStatus.BAD_REQUEST, "기본 게시판은 삭제할 수 없습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
