package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BasicErrorResult implements ErrorResult {
    // 4XX 클라이언트 에러
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    FORBIDDEN_REQUEST(HttpStatus.FORBIDDEN, "요청이 서버에서 거부되었습니다."),
    UNKNOWN_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "알 수 없는 클라이언트 에러입니다."),

    // 5XX 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 처리 중에 오류가 발생했습니다."),
    UNKNOWN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러입니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
