package FIS.iLUVit.event;

import FIS.iLUVit.domain.ExpoToken;
import FIS.iLUVit.event.dto.ExpoServerResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ExpoServerUtils {

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

        String url = "https://exp.host/--/api/v2/push/send"; // 엑스포 서버 주소

        RestTemplate restTemplate = new RestTemplate();

        ExpoServerResponse response = restTemplate.postForObject(url, body, ExpoServerResponse.class);

        return response;
    }
}
