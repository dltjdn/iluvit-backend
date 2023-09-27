package FIS.iLUVit.exception.exceptionHandler.controllerAdvice;

import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import FIS.iLUVit.exception.exceptionHandler.SlackErrorLogger;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {
    private final SlackErrorLogger slackErrorLogger;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        log.error("[InternalExceptionHandler {} {} errMessage={}\n",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getMessage()
        );

        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), httpServletRequest); // 슬랙 알림 보내는 메서드

        return makeErrorResponseEntity(e.getMessage());
    }

    /**
     * validation 에서 Exception 발생시 자동으로 handleMethodArgumentNotValid 호출
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        final List<String> errorList = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("[MethodArgumentNotValidException {} {} errMessage={}\n",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getMessage()
        );

        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), httpServletRequest); // 슬랙 알림 보내는 메서드

        return makeErrorResponseEntity(errorList.toString());
    }

    /**
     * request dto type 불일치 exception
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        log.warn("[HttpMessageNotReadableException {} {} errMessage={}\n",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), httpServletRequest); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final List<String> errorList = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        log.warn("[BindException {} {} errMessage={}\n",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                errorList.get(0)
        );
        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), httpServletRequest); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(errorList.get(0));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> DataIntegrityViolationExHandler(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("[DataIntegrityViolationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getMessage());
    }


    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> illegalExHandler(InvalidDataAccessApiUsageException e, HttpServletRequest request) {
        log.warn("[InvalidDataAccessApiUsageException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> jwtVerificationException(JWTVerificationException e, HttpServletRequest request) {
        log.warn("[JwtVerificationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(HttpStatus.UNAUTHORIZED,e.getMessage());

    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("[AuthenticationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(HttpStatus.UNAUTHORIZED,e.getMessage());
    }

    /**
     * 문자 메세지 관련 에러 등록
     */
    @ExceptionHandler(NurigoBadRequestException.class)
    public ResponseEntity<Object> nurigoException(NurigoBadRequestException e, HttpServletRequest request) {
        log.warn("[NurigoBadRequestException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getMessage());
    }

    /**
     * Presentation 관련 에러 등록
     */
    @ExceptionHandler(PresentationException.class)
    public ResponseEntity<ErrorResponse> PresenterExceptionHandler(PresentationException e, HttpServletRequest request) {
        log.warn("[PresentationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * AuthNumber 관련 에러 등록
     */
    @ExceptionHandler(AuthNumberException.class)
    public ResponseEntity<ErrorResponse> authNumberExceptionHandler(AuthNumberException e, HttpServletRequest request) {
        log.warn("[AuthNumberException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Chat 관련 에러 등록
     */
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> chatExceptionHandler(ChatException e, HttpServletRequest request) {
        log.warn("[ChatException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * BoardBookmark 관련 에러 등록
     */
    @ExceptionHandler(BoardBookmarkException.class)
    public ResponseEntity<ErrorResponse> boardBookmarkException(BoardBookmarkException e, HttpServletRequest request) {
        log.warn("[BoardBookmarkException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }
    /**
     * Board 관련 에러 등록
     */
    @ExceptionHandler(BoardException.class)
    public ResponseEntity<ErrorResponse> boardException(BoardException e, HttpServletRequest request) {
        log.warn("[BoardException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Comment 관련 에러 등록
     */
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> commentException(CommentException e, HttpServletRequest request) {
        log.warn("[CommentException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Post 관련 에러 등록
     */
    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorResponse> postException(PostException e, HttpServletRequest request) {
        log.warn("[PostException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Waiting 관련 에러 등록
     */
    @ExceptionHandler(WaitingException.class)
    public ResponseEntity<ErrorResponse> waitingException(WaitingException e, HttpServletRequest request) {
        log.warn("[WaitingException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Participation 관련 에러 등록
     */
    @ExceptionHandler(ParticipationException.class)
    public ResponseEntity<ErrorResponse> participantException(ParticipationException e, HttpServletRequest request) {
        log.warn("[ParticipationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * User 관련 에러 등록
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> userException(UserException e, HttpServletRequest request) {
        log.warn("[UserException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Review 관련 에러 등록
     */
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ErrorResponse> reviewException(ReviewException e, HttpServletRequest request) {
        log.warn("[ReviewException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Center 관련 에러 등록
     */
    @ExceptionHandler(CenterException.class)
    public ResponseEntity<ErrorResponse> centerException(CenterException e, HttpServletRequest request) {
        log.warn("[CenterException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Scrap 관련 에러 등록
     */
    @ExceptionHandler(ScrapException.class)
    public ResponseEntity<ErrorResponse> scrapException(ScrapException e, HttpServletRequest request) {
        log.warn("[ScrapException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * CenterBookmark 관련 에러 등록
     */
    @ExceptionHandler(CenterBookmarkException.class)
    public ResponseEntity<ErrorResponse> centerBookmarkException(CenterBookmarkException e, HttpServletRequest request) {
        log.warn("[CenterBookmarkException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Report 관련 에러 등록
     */
    @ExceptionHandler(ReportException.class)
    public ResponseEntity<ErrorResponse> reportException(ReportException e, HttpServletRequest request) {
        log.warn("[ReportException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Blocked 관련 에러 등록
     */
    @ExceptionHandler(BlockedException.class)
    public ResponseEntity<ErrorResponse> blockedErrorResult(BlockedException e, HttpServletRequest request) {
        log.warn("[BlockedException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * BlackUser 관련 에러 등록
     */
    @ExceptionHandler(BlackUserException.class)
    public ResponseEntity<ErrorResponse> blockedErrorResult(BlackUserException e, HttpServletRequest request) {
        log.warn("[BlackUserException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Image 관련 에러 등록
     */
    @ExceptionHandler(ImageException.class)
    public ResponseEntity<ErrorResponse> imageErrorResult(ImageException e, HttpServletRequest request) {
        log.warn("[ImageException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * PoliceClient 관련 에러 등록
     */
    @ExceptionHandler(PoliceClientException.class)
    public ResponseEntity<ErrorResponse> policeClientErrorResult(PoliceClientException e, HttpServletRequest request) {
        log.warn("[PoliceClientException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * Data 관련 에러 등록
     */
    @ExceptionHandler(DataException.class)
    public ResponseEntity<ErrorResponse> dataException(DataException e) {
        log.warn("[DataException] ex", e);
        return makeErrorResponseEntity(e.getErrorResult());
    }

    /**
     * create ResponseEntity
     */
    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(ErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage()));
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(httpStatus,message));
    }

    private ResponseEntity<Object> makeErrorResponseEntity(final String errorMessage) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
    }


}