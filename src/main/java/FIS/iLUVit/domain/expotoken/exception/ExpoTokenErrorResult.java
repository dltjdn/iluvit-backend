package FIS.iLUVit.domain.expotoken.exception;

import FIS.iLUVit.global.exception.ErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ExpoTokenErrorResult implements ErrorResult {
    /**
     * 404 NOT FOUND
     */
    EXPO_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 엑스포 토큰이 존재하지 않습니다."),

    /**
     * 403 FORBIDDEN
     */
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없는 유저입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
