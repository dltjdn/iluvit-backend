package FIS.iLUVit.service;

import FIS.iLUVit.dto.alarm.PoliceLoginRequest;
import FIS.iLUVit.dto.alarm.ScheduleByDateResponse;

import FIS.iLUVit.exception.PoliceClientErrorResult;
import FIS.iLUVit.exception.PoliceClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliceClientService {
    private static final String BASE_URL = "https://apipolice.iluvit.app";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String SCHEDULE_URL = BASE_URL + "/schedule";
    private final RestTemplate restTemplate;

    @Value("${application-auth.username}")
    private String username;
    @Value("${application-auth.password}")
    private String password;

    /**
     * 폴리스 서버 로그인 API 호출
     */
    public String loginToPoliceServer() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 로그인 요청에 필요한 데이터 설정 ( 사용자 아이디, 비밀번호 )
        PoliceLoginRequest loginRequest = new PoliceLoginRequest(username, password);
        HttpEntity<PoliceLoginRequest> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Void> responseEntity = restTemplate.exchange(LOGIN_URL, HttpMethod.POST, requestEntity, Void.class);

        // 응답 헤더에서 세션 토큰 추출
        List<String> setCookieHeaders = responseEntity.getHeaders().get("Set-Cookie");
        if (setCookieHeaders != null && !setCookieHeaders.isEmpty()) {
            for (String setCookieHeader : setCookieHeaders) {
                // 헤더에서 세션 토큰 추출
                if (setCookieHeader.startsWith("JSESSIONID")) {
                    String sessionToken = setCookieHeader.split(";")[0]; // 세션 토큰 추출
                    return sessionToken;
                }
            }
        }
        return null;
    }

    /**
     * 폴리스 서버 날짜별 스케줄 조회 API 호출
     */
    public List<ScheduleByDateResponse> getScheduleByDate(LocalDate date) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String sessionToken = loginToPoliceServer();
        headers.add("Cookie", sessionToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);


        ResponseEntity<Map<String, List<ScheduleByDateResponse>>> responseEntity = restTemplate.exchange(
                SCHEDULE_URL + "?date=" + date.toString(),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<Map<String, List<ScheduleByDateResponse>>>() {}
        );

        // 응답 값에서 스케줄 정보를 가져와서 반환
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Map<String, List<ScheduleByDateResponse>> responseData = responseEntity.getBody();
            if (responseData != null && responseData.containsKey("data")) {
                return responseData.get("data");
            } else {
                // "data" 필드가 없거나 비어있는 경우 처리
                return Collections.emptyList();
            }
        } else {
            // API 호출에 실패한 경우에 대한 처리
            throw new PoliceClientException(PoliceClientErrorResult.REQUEST_TIMEOUT);
        }
    }

}
