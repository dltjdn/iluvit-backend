package FIS.iLUVit.domain.child.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ChildErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    CHILD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이가 존재하지 않습니다."),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
