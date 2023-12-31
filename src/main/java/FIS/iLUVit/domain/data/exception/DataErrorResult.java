package FIS.iLUVit.domain.data.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DataErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "검색결과가 없습니다."),

    /**
     * 433 UNPROCESSABLE ENTITY
     */
    JSON_PARSE_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "JSON 파싱에 실패했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

}
