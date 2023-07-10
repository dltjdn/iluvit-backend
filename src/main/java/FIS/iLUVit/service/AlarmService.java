package FIS.iLUVit.service;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    
    /**
        작성날짜: 2023/07/07 7:35 PM
        작성자: 이서우
        작성내용: 활동 알림을 조회합니다
    */
    public Slice<Alarm> findActiveAlarmByUser(Long userId, Pageable pageable) {
        Slice<Alarm> alarms = alarmRepository.findActiveByUser(userId, pageable);

        return alarms;
    }

    /**
        작성날짜: 2023/07/07 7:36 PM
        작성자: 이서우
        작성내용: 설명회 알림을 조회합니다
    */
    public Slice<Alarm> findPresentationActiveAlarmByUser(Long userId, Pageable pageable) {
        Slice<Alarm> alarms = alarmRepository.findPresentationByUser(userId, pageable);

        return alarms;
    }

    /**
        작성날짜: 2023/07/07 7:49 PM
        작성자: 이서우
        작성내용: 전체 알림 읽었다고 처리하기
    */
    public void readAlarm(Long userId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .updateReadAlarm(Boolean.TRUE); // user의 readAlarm 필드를 true로 바꾼다
        
    }

    /**
        작성날짜: 2023/07/07 7:57 PM
        작성자: 이서우
        작성내용: 전체 알림 읽었는지 안 읽었는지 여부를 조회합니다
    */
    public Boolean hasRead(Long userId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        Boolean readAlarm = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .getReadAlarm();

        return readAlarm;
    }

    /**
     작성날짜: 2023/07/07 7:25 PM
     작성자: 이서우
     작성내용: 선택한 알림들을 삭제합니다
     */
    public void deleteSelectedAlarm(Long userId, List<Long> alarmIds) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        alarmRepository.deleteByUserIdAndIdIn(userId, alarmIds);
    }

    /**
     작성날짜: 2023/07/07 7:26 PM
     작성자: 이서우
     작성내용: 모든 알림을 삭제합니다
     */
    public void deleteAllAlarm(Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        alarmRepository.deleteAllByUser(user);

    }

}
