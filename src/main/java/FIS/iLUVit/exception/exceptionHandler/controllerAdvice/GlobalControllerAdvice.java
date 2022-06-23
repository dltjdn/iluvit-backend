package FIS.iLUVit.exception.exceptionHandler.controllerAdvice;

import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResult RuntimeException(RuntimeException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    // repository에서 쿼리 날릴때 parameter가 null이면 생기는 예외(토큰이 유효하지 않아 @Login이 Null일 확률이 높음)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ErrorResult illegalExHandler(InvalidDataAccessApiUsageException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", "쿼리마라미터가 null 입니다. 토큰이 유효한지 확인해보세요");
    }

}
