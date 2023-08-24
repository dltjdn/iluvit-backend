package FIS.iLUVit.exception.exceptionHandler.controllerAdvice;

import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.ErrorResult;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoBadRequestException;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {
    private final Slack slackClient = Slack.getInstance();

    @Value("${dev.webhook-uri}")
    private String webhookUrl;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        log.warn("[InternalExceptionHandler] {}", e);
        sendSlackAlertErrorLog(e.getMessage(), httpServletRequest); // 슬랙 알림 보내는 메서드
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

        log.warn("[MethodArgumentNotValidException] {}", errorList);
        sendSlackAlertErrorLog(e.getMessage(), httpServletRequest); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(errorList.toString());
    }

    /**
     * request dto type 불일치 exception
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        log.warn("[HttpMessageNotReadableException] errMessage={}\n", e.getMessage());
        sendSlackAlertErrorLog(e.getMessage(), httpServletRequest); // 슬랙 알림 보내는 메서드
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

        log.warn("[BindException] errMessage={}\n", errorList.get(0));
        sendSlackAlertErrorLog(e.getMessage(), httpServletRequest); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(errorList.get(0));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> DataIntegrityViolationExHandler(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("[DataIntegrityViolationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getMessage());
    }


    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErrorResponse> illegalExHandler(InvalidDataAccessApiUsageException e, HttpServletRequest request) {
        log.warn("[InvalidDataAccessApiUsageException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> jwtVerificationException(JWTVerificationException e, HttpServletRequest request) {
        log.warn("[JwtVerificationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(HttpStatus.UNAUTHORIZED,e.getMessage());

    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("[AuthenticationException] {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage()
        );
        sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
        return makeErrorResponseEntity(e.getErrorResult());
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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
        sendSlackAlertErrorLog(e.getErrorResult().getMessage(), request); // 슬랙 알림 보내는 메서드
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

    // 슬랙 알림 보내는 메서드
    private void sendSlackAlertErrorLog(String errMessage, HttpServletRequest request) {
        try {
            slackClient.send(webhookUrl, payload(p -> p
                    .text("서버 에러 발생! 백엔드 측의 빠른 확인 요망")
                    // attachment는 list 형태여야 합니다.
                    .attachments(
                            List.of(generateSlackAttachment(errMessage, request))
                    )
            ));
        } catch (IOException slackError) {
            // slack 통신 시 발생한 예외에서 Exception을 던져준다면 재귀적인 예외가 발생합니다.
            // 따라서 로깅으로 처리하였고, 모카콩 서버 에러는 아니므로 `error` 레벨보다 낮은 레벨로 설정했습니다.
            log.debug("Slack 통신과의 예외 발생");
        }
    }

    // attachment 생성 메서드
    private Attachment generateSlackAttachment(String errMessage, HttpServletRequest request) {
        String requestTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
        String xffHeader = request.getHeader("X-FORWARDED-FOR");  // 프록시 서버일 경우 client IP는 여기에 담길 수 있습니다.
        return Attachment.builder()
                .color("ff0000")  // 붉은 색으로 보이도록
                .title(requestTime + " 발생 에러 로그")
                // Field도 List 형태로 담아주어야 합니다.
                .fields(List.of(
                                generateSlackField("Request IP", xffHeader == null ? request.getRemoteAddr() : xffHeader),
                                generateSlackField("Request URL", request.getRequestURL() + " " + request.getMethod()),
                                generateSlackField("Error Message", errMessage)
                        )
                )
                .build();
    }

    // Field 생성 메서드
    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }




}
