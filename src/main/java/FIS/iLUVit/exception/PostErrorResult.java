package FIS.iLUVit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorResult {

    NO_MATCH_ANONYMOUS_INFO(HttpStatus.BAD_REQUEST, "게시글 작성자의 익명 여부와 Request 바디의 익명 여부가 일치하지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
