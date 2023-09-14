package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.alarms.PresentationCreatedAlarm;
import FIS.iLUVit.dto.alarm.AlarmDeleteRequest;
import FIS.iLUVit.dto.alarm.AlarmResponse;
import FIS.iLUVit.dto.alarm.AlarmReadResponse;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.CenterBookmarkRepository;
import FIS.iLUVit.repository.UserRepository;
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
        userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .updateReadAlarm(Boolean.TRUE); // user의 readAlarm 필드를 true로 바꾼다
    }

    /**
     * 전체 알림 읽었는지 안 읽었는지 여부를 조회합니다
     */
    public AlarmReadResponse hasRead(Long userId) {
        Boolean readAlarm = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        alarmRepository.deleteAllByUser(user);
    }

    public void sendPresentationCreatedAlarm(Center center, Presentation presentation) {
        centerBookmarkRepository.findByCenter(center).forEach(prefer -> {
            Alarm alarm = new PresentationCreatedAlarm(prefer.getParent(), presentation, center);
            alarmRepository.save(alarm);
            String type = "아이러빗";
            AlarmUtils.publishAlarmEvent(alarm, type);
        });
    }

}
