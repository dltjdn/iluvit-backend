package FIS.iLUVit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorResult {

    BOARD_NAME_DUPLICATION(HttpStatus.BAD_REQUEST, "해당 이름의 게시판이 이미 존재합니다."),
    UNAUTHORIZED_USER_ACCESS(HttpStatus.BAD_REQUEST, "게시판 생성 권한이 없는 유저입니다."),
    BOARD_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 아이디를 가진 게시판이 존재하지 않습니다"),
    DEFAULT_BOARD_DELETE_BAN(HttpStatus.BAD_REQUEST, "기본 게시판은 삭제할 수 없습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
