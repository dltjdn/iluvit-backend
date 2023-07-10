package FIS.iLUVit.service;

import FIS.iLUVit.domain.User;
import FIS.iLUVit.dto.alarm.AlarmDetailDto;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.AlarmRepository;
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
    public Integer deleteSelectedAlarm(Long userId, List<Long> alarmIds) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return alarmRepository.deleteByIds(userId, alarmIds);
    }

    public void deleteAllAlarm(Long userId){

        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        alarmRepository.deleteAllByUser(user);
    }

    public Slice<AlarmDetailDto> findActiveAlarmByUser(Long userId, Pageable pageable) {
        Slice<Alarm> alarmSlice = alarmRepository.findActiveByUser(userId, pageable);
        return new SliceImpl<>(alarmSlice.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarmSlice.hasNext());
    }

    public Slice<AlarmDetailDto> findPresentationActiveAlarmByUser(Long userId, Pageable pageable) {
        Slice<Alarm> alarmSlice = alarmRepository.findPresentationByUser(userId, pageable);
        return new SliceImpl<>(alarmSlice.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarmSlice.hasNext());
    }

    public void readAlarm(Long userId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .updateReadAlarm(Boolean.TRUE);
    }

    public Boolean hasRead(Long userId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .getReadAlarm();
    }
}
