package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorResult implements ErrorResult {

    NO_MATCH_ANONYMOUS_INFO(HttpStatus.BAD_REQUEST, "게시글 작성자의 익명 여부와 Request 바디의 익명 여부가 일치하지 않습니다."),
    POST_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 아이디를 가진 게시글이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
