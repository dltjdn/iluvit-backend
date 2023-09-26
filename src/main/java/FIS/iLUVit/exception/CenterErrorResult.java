package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CenterErrorResult implements ErrorResult {

    /**
     * 404 NOT FOUND
     */
    CENTER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디를 가진 센터가 존재하지 않습니다."),

    /**
     * 400 BAD REQUEST
     */
    ADDRESS_CONVERSION_FAILED(HttpStatus.BAD_REQUEST, "시설 주소 변환에 실패했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
