package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorResult implements ErrorResult {

    NO_MATCH_ANONYMOUS_INFO(HttpStatus.BAD_REQUEST, "게시글 작성자의 익명 여부와 Request 바디의 익명 여부가 일치하지 않습니다."),
    POST_NOT_EXIST(HttpStatus.I_AM_A_TEAPOT, "해당 아이디를 가진 게시글이 존재하지 않습니다."),
    PARENT_NOT_ACCESS_NOTICE(HttpStatus.FORBIDDEN, "학부모 회원은 공지 게시판에 글을 작성할 수 없습니다"),
    UNAUTHORIZED_USER_ACCESS(HttpStatus.FORBIDDEN, "권한 없는 유저입니다."),
    ALREADY_EXIST_HEART(HttpStatus.BAD_REQUEST, "이미 좋아요한 게시글입니다."),
    WAITING_OR_REJECT_CANNOT_ACCESS(HttpStatus.FORBIDDEN, "해당 시설에 대해 권한이 없거나 승인 대기 혹은 거부된 회원의 요청입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
