package FIS.iLUVit.exception.exceptionHandler.controllerAdvice;

import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoBadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
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

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("[ResponseEntityExceptionHandler] {}", ex);
        return makeErrorResponseEntity(ex.getMessage());
    }

    /**
     * validation 에서 Exception 발생시 자동으로 handleMethodArgumentNotValid 호출
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("[Invalid Dto Parameter errors] {}", errorList);
        return makeErrorResponseEntity(errorList.toString());
    }

    /**
     * request dto type 불일치 exception
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.warn("[Request type mapping error] {}", ex);
        return makeErrorResponseEntity("HttpMessageNotReadable error");
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("[Request type mapping errors] {}", errorList);
        return makeErrorResponseEntity(errorList.get(0));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> DataIntegrityViolationExHandler(DataIntegrityViolationException e) {
        log.warn("[DataIntegrityViolationException] {}", e);
        return makeErrorResponseEntity("올바르지 않은 식별자값입니다.");
    }

    /**
     *  repository에서 쿼리 날릴때 parameter가 null이면 생기는 예외(토큰이 유효하지 않아 @Login이 Null일 확률이 높음)
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> illegalExHandler(InvalidDataAccessApiUsageException e) {
        log.warn("[ExceptionHandler] {}", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN, "쿼리파라미터가 null 입니다. 토큰이 유효한지 확인해보세요"));
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> jwtVerificationException(JWTVerificationException e) {
        log.warn("[JwtVerificationException] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationException(AuthenticationException e) {
         log.warn("[AuthenticationException] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 잘못되었습니다."));
    }

    /**
     * 문자 메세지 관련 에러 등록
     */
    @ExceptionHandler(NurigoBadRequestException.class)
    public ResponseEntity<Object> nurigoException(NurigoBadRequestException e) {
        log.warn("[NurigoBadRequestExceptionHandler] {}", e);
        return makeErrorResponseEntity(e.getMessage());
    }

    /**
     * Presentation 관련 에러 등록
     */
    @ExceptionHandler(PresentationException.class)
    public ResponseEntity<ErrorResponse> PresenterExceptionHandler(PresentationException e) {
        log.warn("[PresentationExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * AuthNumber 관련 에러 등록
     */
    @ExceptionHandler(AuthNumberException.class)
    public ResponseEntity<ErrorResponse> authNumberExceptionHandler(AuthNumberException e) {
        log.warn("[AuthNumberExceptionHandler] {}", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Signup 관련 에러 등록
     */
    @ExceptionHandler(SignupException.class)
    public ResponseEntity<ErrorResponse> signupExceptionHandler(SignupException e) {
        log.warn("[SignupExceptionHandler] {}", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Chat 관련 에러 등록
     */
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> chatExceptionHandler(ChatException e) {
        log.warn("[ChatExceptionHandler] {}", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Bookmark 관련 에러 등록
     */
    @ExceptionHandler(BookmarkException.class)
    public ResponseEntity<ErrorResponse> bookmarkException(BookmarkException e) {
        log.warn("[BookmarkException] {}", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }
    /**
     * Board 관련 에러 등록
     */
    @ExceptionHandler(BoardException.class)
    public ResponseEntity<ErrorResponse> boardException(BoardException e) {
        log.warn("[BoardException] {}", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Comment 관련 에러 등록
     */
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> commentException(CommentException e) {
        log.warn("[CommentExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Post 관련 에러 등록
     */
    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorResponse> postException(PostException e) {
        log.warn("[PostExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Waiting 관련 에러 등록
     */
    @ExceptionHandler(WaitingException.class)
    public ResponseEntity<ErrorResponse> waitingException(WaitingException e) {
        log.warn("[WaitingExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Participation 관련 에러 등록
     */
    @ExceptionHandler(ParticipationException.class)
    public ResponseEntity<ErrorResponse> participantException(ParticipationException e) {
        log.warn("[WaitingExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * User 관련 에러 등록
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> userException(UserException e) {
        log.warn("[UserExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Review 관련 에러 등록
     */
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ErrorResponse> reviewException(ReviewException e) {
        log.warn("[ReviewExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Center 관련 에러 등록
     */
    @ExceptionHandler(CenterException.class)
    public ResponseEntity<ErrorResponse> centerException(CenterException e) {
        log.warn("[CenterExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Scrap 관련 에러 등록
     */
    @ExceptionHandler(ScrapException.class)
    public ResponseEntity<ErrorResponse> scrapException(ScrapException e) {
        log.warn("[ScrapExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Prefer 관련 에러 등록
     */
    @ExceptionHandler(PreferException.class)
    public ResponseEntity<ErrorResponse> preferException(PreferException e) {
        log.warn("[PreferExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Report 관련 에러 등록
     */
    @ExceptionHandler(ReportException.class)
    public ResponseEntity<ErrorResponse> reportException(ReportException e) {
        log.warn("[ReportExceptionHandler] {}", e.getMessage());
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Blocked 관련 에러 등록
     */
    @ExceptionHandler(BlockedException.class)
    public ResponseEntity<ErrorResponse> blockedErrorResult(BlockedException e) {
        log.warn("[BlockedErrorResult] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * BlackUser 관련 에러 등록
     */
    @ExceptionHandler(BlackUserException.class)
    public ResponseEntity<ErrorResponse> blockedErrorResult(BlackUserException e) {
        log.warn("[BlackUserException] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Image 관련 에러 등록
     */
    @ExceptionHandler(ImageException.class)
    public ResponseEntity<ErrorResponse> imageErrorResult(ImageException e) {
        log.warn("[ImageException] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * PoliceClient 관련 에러 등록
     */
    @ExceptionHandler(PoliceClientException.class)
    public ResponseEntity<ErrorResponse> policeClientErrorResult(PoliceClientException e) {
        log.warn("[PoliceClientException] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * ErrorResult -> ErrorResponse
     */
    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(ErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage()));
    }

    private ResponseEntity<Object> makeErrorResponseEntity(final String errorDescription) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorDescription));
    }


}
