package FIS.iLUVit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CenterErrorResult {
    CENTER_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 아이디를 가진 센터가 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
