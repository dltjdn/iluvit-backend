package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorResult implements ErrorResult {

    NO_EXIST_COMMENT(HttpStatus.BAD_REQUEST, "해당 아이디를 가진 댓글이 존재하지 않습니다."),
    NO_MATCH_ANONYMOUS_INFO(HttpStatus.BAD_REQUEST, "댓글 작성자의 익명 여부와 Request 바디의 익명 여부가 일치하지 않습니다."),
    UNAUTHORIZED_USER_ACCESS(HttpStatus.BAD_REQUEST, "권한이 없는 유저입니다."),
    UNAUTHORIZED_USER_ACCESS_HEART(HttpStatus.BAD_REQUEST, "좋아요 권한이 없는 비회원 유저입니다."),
    ALREADY_EXIST_HEART(HttpStatus.BAD_REQUEST, "이미 좋아요한 댓글입니다."),
    NO_EXIST_COMMENT_HEART(HttpStatus.BAD_REQUEST, "해당 아이디를 가진 좋아요가 존재하지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

}
