package FIS.iLUVit.exception;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BasicErrorResult implements ErrorResult {
    /**
     * 인증 관련 에러
     */
    JWT_VERIFICATION_EXCEPTION(HttpStatus.UNAUTHORIZED, "JWT 검증 예외가 발생했습니다."),
    AUTHENTICATION_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증 예외가 발생했습니다."),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 잘못되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    /**
     * 4XX 클라이언트 에러
     */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    FORBIDDEN_REQUEST(HttpStatus.FORBIDDEN, "요청이 서버에서 거부되었습니다."),
    UNKNOWN_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "알 수 없는 클라이언트 에러입니다."),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "메서드 인자가 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "HTTP 메시지를 읽을 수 없습니다."),
    BIND_EXCEPTION(HttpStatus.BAD_REQUEST, "데이터 바인딩에 실패했습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "데이터 무결성 위반 예외가 발생했습니다."),
    INVALID_DATA_ACCESS(HttpStatus.FORBIDDEN, "잘못된 데이터 액세스 예외가 발생했습니다."),
    NURIGO_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Nurigo 관련 잘못된 요청 예외가 발생했습니다."),

    /**
     * 5XX 서버 에러
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 처리 중에 오류가 발생했습니다."),
    UNKNOWN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러입니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");

    ;
    private final HttpStatus httpStatus;
    private final String message;
}
