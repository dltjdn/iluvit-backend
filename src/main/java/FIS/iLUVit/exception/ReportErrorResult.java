package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorResult implements ErrorResult {

    ALREADY_EXIST_POST_REPORT(HttpStatus.BAD_REQUEST, "이미 신고한 게시글입니다."),
    ALREADY_EXIST_COMMENT_REPORT(HttpStatus.BAD_REQUEST, "이미 신고한 댓글입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
