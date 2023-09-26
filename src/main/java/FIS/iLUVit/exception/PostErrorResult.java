package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUNd
     */
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 게시글이 존재하지 않습니다."),
    POST_HEART_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 아이디를 가진 좋아요가 존재하지 않습니다."),

    /**
     * 403 FORBIDDEN
     */
    PARENT_CANNOT_WRITE_NOTICE(HttpStatus.FORBIDDEN, "학부모 회원은 공지 게시판에 글을 작성할 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),


    /**
     * 400 BAD REQUEST
     */
    ALREADY_HEART_POST(HttpStatus.BAD_REQUEST, "이미 좋아요한 게시글입니다."),
    MISSING_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
