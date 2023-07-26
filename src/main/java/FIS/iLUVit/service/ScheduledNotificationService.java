package FIS.iLUVit.service;

import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.alarms.AgentVisitedAlarm;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.dto.alarm.ScheduleByDateResponse;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledNotificationService {
    private final PoliceClientService policeClientService;
    private final TeacherRepository teacherRepository;
    private final AlarmRepository alarmRepository;

    /**
     * 내일 현장요원이 방문하는 시설의 관리교사에게 알림 보내기
     */
//    @Scheduled(cron = "0 0 10 * * ?")
    @Scheduled(fixedRate = 600000)
    public void sendRegularAgentVisitNotification() {
        // 내일 날짜 계산
//        LocalDate tomorrow = LocalDate.now().plus(1, ChronoUnit.DAYS);
        LocalDate tomorrow = LocalDate.of(2023, 7, 26);

        // 내일 날짜에 대한 스케줄 데이터 가져오기
        List<ScheduleByDateResponse> scheduleResponses = policeClientService.getScheduleByDate(tomorrow);
        log.info("내일 스케줄 받아오기");

        // 관리교사에게 알림 보내기
        for (ScheduleByDateResponse response : scheduleResponses) {
            Long centerId = response.getCenter_id();
            log.info("시설 아이디 = " + centerId);
            List<Teacher> directors = Optional.ofNullable(teacherRepository.findDirectorByCenter(centerId))
                            .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
            log.info("관리교사 리스트와 시설 아이디" + directors.toString() + centerId);
            directors.forEach(director -> {
                Alarm alarm = new AgentVisitedAlarm(director, Auth.DIRECTOR, director.getCenter());
                log.info("관리교사 로그인아이디 = " + director.getLoginId());
                alarmRepository.save(alarm);
                AlarmUtils.publishAlarmEvent(alarm);
            });
        }
    }
}
