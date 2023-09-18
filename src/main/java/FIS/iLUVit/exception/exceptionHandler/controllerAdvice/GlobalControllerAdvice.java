package FIS.iLUVit.exception.exceptionHandler.controllerAdvice;

import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.SlackErrorLogger;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoBadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final SlackErrorLogger slackErrorLogger;

    /**
     * 모든 일반적인 예외에 대한 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleExceptionInternal(Exception e, HttpServletRequest request) {
        log.error("[InternalExceptionHandler {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );

        slackErrorLogger.sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드

        return ErrorResponse.toResponseEntity(BasicErrorResult.INTERNAL_SERVER_ERROR);
    }

    /**
     * 메서드 인자로 전달된 데이터가 유효하지 않을 때
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {

        final List<String> errorList = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("[MethodArgumentNotValidException {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                errorList.get(0)
        );

        slackErrorLogger.sendSlackAlertWarnLog(errorList.get(0), request); // 슬랙 알림 보내는 메서드

        return ErrorResponse.toResponseEntity(BasicErrorResult.METHOD_ARGUMENT_NOT_VALID);
    }


    /**
     *  HTTP 메시지가 읽을 수 없을 때
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("[HttpMessageNotReadableException {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );

        slackErrorLogger.sendSlackAlertWarnLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드

        return ErrorResponse.toResponseEntity(BasicErrorResult.HTTP_MESSAGE_NOT_READABLE);
    }

    /**
     * 데이터 바인딩에 실패했을 때
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        final List<String> errorList = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("[BindException {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                errorList.get(0)
        );

        slackErrorLogger.sendSlackAlertWarnLog(errorList.get(0), request); // 슬랙 알림 보내는 메서드

        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, errorList.get(0));
    }


    /**
     * 데이터 무결성 위반 예외 처리
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> DataIntegrityViolationExHandler(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("[DataIntegrityViolationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return ErrorResponse.toResponseEntity(BasicErrorResult.DATA_INTEGRITY_VIOLATION);
    }

    /**
     * 잘못된 데이터 액세스 사용 예외
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> illegalExHandler(InvalidDataAccessApiUsageException e, HttpServletRequest request) {
        log.warn("[InvalidDataAccessApiUsageException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return ErrorResponse.toResponseEntity(BasicErrorResult.INVALID_DATA_ACCESS);
    }

    /**
     * JWT 검증 예외
     */
    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> jwtVerificationException(JWTVerificationException e, HttpServletRequest request) {
        log.warn("[JwtVerificationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return ErrorResponse.toResponseEntity(BasicErrorResult.JWT_VERIFICATION_EXCEPTION);

    }

    /**
     * 인증 예외
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("[AuthenticationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return ErrorResponse.toResponseEntity(BasicErrorResult.AUTHENTICATION_EXCEPTION);
    }

    /**
     * 문자 메세지 관련 에러 등록
     */
    @ExceptionHandler(NurigoBadRequestException.class)
    public ResponseEntity<ErrorResponse> nurigoException(NurigoBadRequestException e, HttpServletRequest request) {
        log.warn("[NurigoBadRequestException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return ErrorResponse.toResponseEntity(BasicErrorResult.NURIGO_BAD_REQUEST);
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
    }

    /**
     * Signup 관련 에러 등록
     */
    @ExceptionHandler(SignupException.class)
    public ResponseEntity<ErrorResponse> signupExceptionHandler(SignupException e, HttpServletRequest request) {
        log.warn("[SignupException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getErrorResult().getMessage()
        );
        slackErrorLogger.sendSlackAlertWarnLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
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
        return ErrorResponse.toResponseEntity(e.getErrorResult());
    }


}
