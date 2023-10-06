package FIS.iLUVit.domain.alarm.service;

import FIS.iLUVit.domain.alarm.AlarmUtils;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.alarm.domain.AgentVisitedAlarm;
import FIS.iLUVit.domain.alarm.domain.Alarm;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.common.domain.NotificationTitle;
import FIS.iLUVit.domain.alarm.dto.ScheduleByDateResponse;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.alarm.repository.AlarmRepository;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledNotificationService {
    private final PoliceClientService policeClientService;
    private final TeacherRepository teacherRepository;
    private final AlarmRepository alarmRepository;
    private final CenterRepository centerRepository;

    /**
     * 내일 현장요원이 방문하는 시설의 관리교사에게 알림 보내기
     */
    @Scheduled(cron = "0 0 15 * * ?")
    public void sendRegularAgentVisitNotification() {
        // 내일 날짜 계산
        LocalDate tomorrow = LocalDate.now().plus(1, ChronoUnit.DAYS);

        // 내일 날짜에 대한 스케줄 데이터 가져오기
        List<ScheduleByDateResponse> scheduleResponses = policeClientService.getScheduleByDate(tomorrow);

        // 관리교사에게 알림 보내기
        for (ScheduleByDateResponse response : scheduleResponses) {
            Long centerId = response.getCenterId();
            Center center = centerRepository.findById(centerId)
                    .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
            List<Teacher> directors = teacherRepository.findByCenterAndAuth(center, Auth.DIRECTOR);
            directors.forEach(director -> {
                Alarm alarm = new AgentVisitedAlarm(director, Auth.DIRECTOR, director.getCenter());
                alarmRepository.save(alarm);
                AlarmUtils.publishAlarmEvent(alarm, NotificationTitle.FINGERPRINT.getDescription());
            });
        }
    }
}
