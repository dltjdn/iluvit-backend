package FIS.iLUVit.domain.comment.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 댓글이 존재하지 않습니다."),
    COMMENT_HEART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 좋아요가 존재하지 않습니다."),

    /**
     * 403 FORBIDDEN
     */
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),

    /**
     * 400 BAD REQEUST
     */
    ALREADY_HEART_COMMENT(HttpStatus.BAD_REQUEST, "이미 좋아요한 댓글입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

}
