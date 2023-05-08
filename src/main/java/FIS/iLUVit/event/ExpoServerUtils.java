package FIS.iLUVit.event;

import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.event.dto.ExpoServerResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpoServerUtils {
    private static String expoUrl;

    @Value("${expo.domain}")
    private void setExpoUrl(String expoUrl){
        this.expoUrl = expoUrl;
    }
    @Data
    static class RequestBody {
        private List<String> to;
        private String title;
        private String body;
    }

    public static ExpoServerResponse sendToExpoServer(List<ExpoToken> expoTokens, String message) {
        // accept true 인 것만 모으기
        List<String> recipients = expoTokens.stream()
                .filter(ExpoToken::getAccept)
                .map(ExpoToken::getToken)
                .collect(Collectors.toList());

        if (recipients.isEmpty()) {
            return null;
        }

        String title = "아이러빗 알림";

        RequestBody body = new RequestBody();
        body.setTo(recipients);
        body.setTitle(title);
        body.setBody(message);

        RestTemplate restTemplate = new RestTemplate();

        ExpoServerResponse response = restTemplate.postForObject(expoUrl, body, ExpoServerResponse.class);

        return response;
    }
}
