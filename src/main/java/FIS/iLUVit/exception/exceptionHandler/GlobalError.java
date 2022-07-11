package FIS.iLUVit.exception.exceptionHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum GlobalError {

    UNUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인을 해주세요."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
