package FIS.iLUVit.domain.alarm.service;

import FIS.iLUVit.domain.alarm.AlarmUtils;
import FIS.iLUVit.domain.alarm.domain.Alarm;
import FIS.iLUVit.domain.alarm.domain.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.alarm.domain.ConvertedToParticipateAlarm;
import FIS.iLUVit.domain.alarm.domain.PresentationCreatedAlarm;
import FIS.iLUVit.domain.alarm.dto.AlarmDeleteRequest;
import FIS.iLUVit.domain.alarm.dto.AlarmReadResponse;
import FIS.iLUVit.domain.alarm.dto.AlarmResponse;
import FIS.iLUVit.domain.alarm.repository.AlarmRepository;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.centerbookmark.repository.CenterBookmarkRepository;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.common.domain.NotificationTitle;
import FIS.iLUVit.domain.presentation.domain.Presentation;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.waiting.domain.Waiting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final CenterBookmarkRepository centerBookmarkRepository;

    /**
     * 활동 알림을 조회합니다
     */
    public Slice<AlarmResponse> findActiveAlarmByUser(Long userId, Pageable pageable) {
        Slice<Alarm> alarms = alarmRepository.findActiveByUser(userId, pageable);
        SliceImpl<AlarmResponse> alarmDetailDtos = new SliceImpl<>(alarms.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarms.hasNext());

        return alarmDetailDtos;
    }

    /**
     * 설명회 알림을 조회합니다
     */
    public Slice<AlarmResponse> findPresentationActiveAlarmByUser(Long userId, Pageable pageable) {
        Slice<Alarm> alarms = alarmRepository.findPresentationByUser(userId, pageable);

        SliceImpl<AlarmResponse> alarmDetailDtos = new SliceImpl<>(alarms.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarms.hasNext());

        return alarmDetailDtos;
    }

    /**
     * 전체 알림 읽었다고 처리하기
     */
    public void readAlarm(Long userId) {
        getUser(userId)
                .updateReadAlarm(Boolean.TRUE); // user의 readAlarm 필드를 true로 바꾼다
    }

    /**
     * 전체 알림 읽었는지 안 읽었는지 여부를 조회합니다
     */
    public AlarmReadResponse hasRead(Long userId) {
        Boolean readAlarm = getUser(userId)
                .getReadAlarm();

        AlarmReadResponse alarmReadResponse = new AlarmReadResponse(readAlarm);

        return alarmReadResponse;
    }

    /**
     * 선택한 알림들을 삭제합니다
     */
    public void deleteSelectedAlarm(Long userId, AlarmDeleteRequest alarmDeleteRequest) {
        List<Long> alarmIds = alarmDeleteRequest.getAlarmIds();

        alarmRepository.deleteByUserIdAndIdIn(userId, alarmIds);
    }

    /**
     * 모든 알림을 삭제합니다
     */
    public void deleteAllAlarm(Long userId) {
        User user = getUser(userId);
        alarmRepository.deleteAllByUser(user);
    }

    /**
     * 설명회 생성 알람을 전송합니다
     */
    public void sendPresentationCreatedAlarms(Center center, Presentation presentation) {
        centerBookmarkRepository.findByCenter(center).forEach(prefer -> {
            Alarm alarm = new PresentationCreatedAlarm(prefer.getParent(), presentation, center);
            alarmRepository.save(alarm);
            String type = "아이러빗";
            AlarmUtils.publishAlarmEvent(alarm, type);
        });
    }

    /**
     * 설명회 참여 알림을 전송합니다
     */
    public void sendParticipateAlarms(List<Waiting> waitings, Presentation presentation){
        waitings.forEach(waiting -> {
            Alarm alarm = new ConvertedToParticipateAlarm(waiting.getParent(), presentation, presentation.getCenter());
            alarmRepository.save(alarm);
            String type = "아이러빗";
            AlarmUtils.publishAlarmEvent(alarm, type);

        });
    }

    public void sendParticipateAlarm(Waiting waiting, Presentation presentation) {
        Alarm alarm = new ConvertedToParticipateAlarm(waiting.getParent(), presentation, presentation.getCenter());
        alarmRepository.save(alarm);
        String type = "아이러빗";
        AlarmUtils.publishAlarmEvent(alarm, type);
    }

    /**
     * 승인 요청 알림을 해당 시설의 관리교사에게 전송
     */
    public void sendCenterApprovalReceivedAlarm(List<Teacher> directors) {
        directors.forEach(director -> {
            Alarm alarm = new CenterApprovalReceivedAlarm(director, Auth.TEACHER, director.getCenter());
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm, NotificationTitle.ILUVIT.getDescription());
        });

    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }



}
