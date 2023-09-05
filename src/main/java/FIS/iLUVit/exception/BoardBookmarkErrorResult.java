package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardBookmarkErrorResult implements ErrorResult {
    USER_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 유저가 존재하지 않습니다."),
    BOARD_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 게시판이 존재하지 않습니다."),
    BOOKMARK_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 북마크가 존재하지 않습니다."),
    UNAUTHORIZED_USER_ACCESS(HttpStatus.FORBIDDEN, "북마크 삭제 권한이 없는 유저입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
