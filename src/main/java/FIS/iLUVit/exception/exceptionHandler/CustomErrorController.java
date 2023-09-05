package FIS.iLUVit.exception.exceptionHandler;

import FIS.iLUVit.exception.BasicErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomErrorController extends AbstractErrorController {
    private final SlackErrorLogger slackErrorLogger;

    public CustomErrorController(ErrorAttributes errorAttributes, SlackErrorLogger slackErrorLogger) {
        super(errorAttributes);
        this.slackErrorLogger = slackErrorLogger;
    }

    @RequestMapping
    public ResponseEntity<ErrorResponse> customError(HttpServletRequest request) {
        HttpStatus httpStatus = getStatus(request);
        String error = getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)).get("message").toString();
        ErrorResult errorResult;

        log.error("[BasicErrorHandler {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                error);

        if (httpStatus.is4xxClientError()) { // 4XX 에러 발생
            errorResult = map4xxClientError(httpStatus);
            slackErrorLogger.sendSlackAlertWarnLog(error,request);
        } else if (httpStatus.is5xxServerError()) { // 5XX 에러 발생
            errorResult = map5xxServerError(httpStatus);
            slackErrorLogger.sendSlackAlertErrorLog(error,request);
        } else {
            errorResult = BasicErrorResult.UNKNOWN_ERROR;
        }

        return ErrorResponse.toResponseEntity(errorResult);
    }

    private ErrorResult map4xxClientError(HttpStatus httpStatus) {
        switch (httpStatus) {
            case BAD_REQUEST:
                return BasicErrorResult.INVALID_REQUEST;
            case NOT_FOUND:
                return BasicErrorResult.RESOURCE_NOT_FOUND;
            case FORBIDDEN:
                return BasicErrorResult.FORBIDDEN_REQUEST;
            default:
                return BasicErrorResult.UNKNOWN_CLIENT_ERROR;
        }
    }

    private ErrorResult map5xxServerError(HttpStatus httpStatus) {
        switch (httpStatus) {
            case INTERNAL_SERVER_ERROR:
                return BasicErrorResult.INTERNAL_SERVER_ERROR;
            default:
                return BasicErrorResult.UNKNOWN_SERVER_ERROR;
        }
    }






}
