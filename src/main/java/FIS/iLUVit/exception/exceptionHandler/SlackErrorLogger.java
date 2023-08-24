package FIS.iLUVit.exception.exceptionHandler;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.slack.api.webhook.WebhookPayloads.payload;
@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SlackErrorLogger {

    private final Slack slackClient = Slack.getInstance();

    @Value("${webhook-uri}")
    private String webhookUrl;

    /**
     * 서버 에러 전송
     */
    public void sendSlackAlertErrorLog(String errMessage, HttpServletRequest request) {
        try {

            slackClient.send(webhookUrl, payload(p -> p
                    .text("서버 에러 발생! 백엔드 측의 빠른 확인 요망")
                    // attachment는 list 형태여야 합니다.
                    .attachments(
                            List.of(generateSlackAttachment(errMessage, request, "ff0000" ))
                    )
            ));
        } catch (IOException slackError) {
            // slack 통신 시 발생한 예외에서 Exception을 던져준다면 재귀적인 예외가 발생합니다.
            // 따라서 로깅으로 처리하였고, 모카콩 서버 에러는 아니므로 `error` 레벨보다 낮은 레벨로 설정했습니다.
            log.debug("Slack 통신과의 예외 발생");
        }
    }

    /**
     * 클라이언트 에러 전송
     */
    public void sendSlackAlertWarnLog(String errMessage, HttpServletRequest request) {
        try {

            slackClient.send(webhookUrl, payload(p -> p
                    .text("클라이언트 에러 발생! 프론트엔드 측의 빠른 확인 요망")
                    // attachment는 list 형태여야 합니다.
                    .attachments(
                            List.of(generateSlackAttachment(errMessage, request,"ffff00" ))
                    )
            ));
        } catch (IOException slackError) {
            // slack 통신 시 발생한 예외에서 Exception을 던져준다면 재귀적인 예외가 발생합니다.
            // 따라서 로깅으로 처리하였고, 모카콩 서버 에러는 아니므로 `error` 레벨보다 낮은 레벨로 설정했습니다.
            log.debug("Slack 통신과의 예외 발생");
        }
    }


    // attachment 생성 메서드
    private Attachment generateSlackAttachment(String errMessage, HttpServletRequest request, String color) {
        String requestTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
        String xffHeader = request.getHeader("X-FORWARDED-FOR");  // 프록시 서버일 경우 client IP는 여기에 담길 수 있습니다.
        return Attachment.builder()
                .color(color)  // 왼쪽 띠의 색
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
