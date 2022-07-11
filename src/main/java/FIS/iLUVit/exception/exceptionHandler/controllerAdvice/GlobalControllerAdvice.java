package FIS.iLUVit.exception.exceptionHandler.controllerAdvice;

import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    /**
     * validation 에서 Exception 발생시 자동으로 handleMethodArgumentNotValid 호출
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Invalid DTO Parameter errors : {}", errorList);
        return this.makeErrorResponseEntity(errorList.toString());
    }

    private ResponseEntity<Object> makeErrorResponseEntity(final String errorDescription) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResult(HttpStatus.BAD_REQUEST.toString(), errorDescription));
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(RuntimeException.class)
//    public ErrorResult RuntimeException(RuntimeException e) {
//        log.error("[exceptionHandler] ex", e);
//        return new ErrorResult("BAD", e.getMessage());
//    }

    // repository에서 쿼리 날릴때 parameter가 null이면 생기는 예외(토큰이 유효하지 않아 @Login이 Null일 확률이 높음)
    //
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ErrorResult illegalExHandler(InvalidDataAccessApiUsageException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult(HttpStatus.FORBIDDEN.toString(), "쿼리파라미터가 null 입니다. 토큰이 유효한지 확인해보세요");
    }

    private ResponseEntity<ErrorResult> makeErrorResponseEntity(PresentationErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResult(errorResult.getHttpStatus().toString(), errorResult.getMessage()));
    }

    @ExceptionHandler(PresentationException.class)
    public ResponseEntity<ErrorResult> PresenterExceptionHandler(PresentationException e){
        log.error("");
        return makeErrorResponseEntity(e.getErrorResult());
    }
}
