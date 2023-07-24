package FIS.iLUVit.service;

import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.alarms.AgentVisitedAlarm;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.dto.alarm.PoliceLoginRequest;
import FIS.iLUVit.dto.alarm.ScheduleByDateResponse;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.TeacherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PoliceClientService {
    private static final String BASE_URL = "https://apipolice.iluvit.app";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String SCHEDULE_URL = BASE_URL + "/schedule";
    private final RestTemplate restTemplate;
    private TeacherRepository teacherRepository;
    private AlarmRepository alarmRepository;

    @Value("${police-admin.username}")
    private String username;
    @Value("${police-admin.password}")
    private String password;

    @Autowired
    public PoliceClientService() {
        this.restTemplate = new RestTemplate();
    }

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

        // 응답 헤더에서 세션 정보 추출
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        String sessionToken = responseHeaders.getFirst("Set-Cookie"); // 세션 정보 추출

        return sessionToken;
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

        ResponseEntity<List<ScheduleByDateResponse>> responseEntity = restTemplate.exchange(
                SCHEDULE_URL + "?date=" + date.toString(),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ScheduleByDateResponse>>() {
                });

        // 응답 값에서 스케줄 정보를 가져와서 반환
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            // API 호출에 실패한 경우에 대한 처리
            throw new RuntimeException("API 요청 실패");
        }
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendRegularAgentVisitNotification() {
        // 내일 날짜 계산
        LocalDate tomorrow = LocalDate.now().plus(1, ChronoUnit.DAYS);

        // 내일 날짜에 대한 스케줄 데이터 가져오기
        List<ScheduleByDateResponse> scheduleResponses = getScheduleByDate(tomorrow);

        // 관리교사에게 알림 보내기
        for (ScheduleByDateResponse response : scheduleResponses) {
            Long centerId = response.getCenter_id();
            List<Teacher> directors = teacherRepository.findDirectorByCenter(centerId);
            directors.forEach(director -> {
                Alarm alarm = new AgentVisitedAlarm(director, Auth.DIRECTOR, director.getCenter());
                alarmRepository.save(alarm);
                AlarmUtils.publishAlarmEvent(alarm);
            });
        }
    }

}
