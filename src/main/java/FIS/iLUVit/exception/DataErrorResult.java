package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DataErrorResult implements ErrorResult {
    JSON_PARSE_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "JSON 파싱에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
