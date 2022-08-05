package FIS.iLUVit.exception.exceptionHandler.controllerAdvice;

import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoBadRequestException;
import net.nurigo.sdk.message.exception.NurigoException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        log.warn("[ResponseEntityExceptionHandler] e : ", ex);
        return this.makeErrorResponseEntity(ex.getMessage());
    }

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

    // request dto type 불일치 exception
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.warn("request type mapping error : ", ex);
        return this.makeErrorResponseEntity("HttpMessageNotReadable error");
    }

    // param(@ModelAttribute) validation exception
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Invalid DTO Parameter errors : {}", errorList);
        return this.makeErrorResponseEntity(errorList.get(0));
    }

    private ResponseEntity<Object> makeErrorResponseEntity(final String errorDescription) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorDescription));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> DataIntegrityViolationExHandler(DataIntegrityViolationException e) {
        log.error("[DataIntegrityViolationException] 올바르지 않은 식별자값입니다.", e);
        return makeErrorResponseEntity("올바르지 않은 식별자값입니다.");
    }

    // repository에서 쿼리 날릴때 parameter가 null이면 생기는 예외(토큰이 유효하지 않아 @Login이 Null일 확률이 높음)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ErrorResponse illegalExHandler(InvalidDataAccessApiUsageException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResponse(HttpStatus.FORBIDDEN, "쿼리파라미터가 null 입니다. 토큰이 유효한지 확인해보세요");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JWTVerificationException.class)
    public ErrorResponse jwtVerificationException(JWTVerificationException e) {
        log.warn("[JWTVerificationException Handler] {}", e.getMessage());
        return new ErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse authenticationException(AuthenticationException e) {
        log.warn("[AuthenticationException Handler] {}", e.getMessage());
        return new ErrorResponse(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 잘못되었습니다.");
    }



    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(ErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage()));
    }

    @ExceptionHandler(PresentationException.class)
    public ResponseEntity<ErrorResponse> PresenterExceptionHandler(PresentationException e) {
        log.error("");
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(AuthNumberException.class)
    public ResponseEntity<ErrorResponse> authNumberExceptionHandler(AuthNumberException e) {
        log.warn("[authNumberExceptionHandler] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(SignupException.class)
    public ResponseEntity<ErrorResponse> signupExceptionHandler(SignupException e) {
        log.warn("[signupExceptionHandler] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> chatExceptionHandler(ChatException e) {
        log.warn("[chatExceptionHandler] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(BookmarkException.class)
    public ResponseEntity<ErrorResponse> bookmarkException(BookmarkException e) {
        log.warn("[BookmarkException] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(BoardException.class)
    public ResponseEntity<ErrorResponse> boardException(BoardException e) {
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> commentException(CommentException e) {
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorResponse> postException(PostException e) {
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(WaitingException.class)
    public ResponseEntity<ErrorResponse> waitingException(WaitingException e) {
        log.error("[WaitingExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(ParticipationException.class)
    public ResponseEntity<ErrorResponse> participantException(ParticipationException e) {
        log.error("[WaitingExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> userException(UserException e) {
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ErrorResponse> reviewException(ReviewException e) {
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(CenterException.class)
    public ResponseEntity<ErrorResponse> centerException(CenterException e) {
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(ScrapException.class)
    public ResponseEntity<ErrorResponse> scrapException(ScrapException e) {
        return makeErrorResponseEntity(e.getErrorResult());
    }

    @ExceptionHandler(NurigoBadRequestException.class)
    public ResponseEntity<Object> nurigoException(NurigoBadRequestException e) {
        return makeErrorResponseEntity("핸드폰번호를 확인해주세요");
    }



}
