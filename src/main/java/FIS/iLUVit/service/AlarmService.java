package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.AlarmDto;
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
    public Integer deleteUserAlarm(Long userId, List<Long> alarmIds) {
        return alarmRepository.deleteByIds(userId, alarmIds);
    }

    public Slice<AlarmDto> findUserActiveAlarm(Long userId, Pageable pageable) {
        Slice<Alarm> alarmSlice = alarmRepository.findActiveByUser(userId, pageable);
        return new SliceImpl<>(alarmSlice.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarmSlice.hasNext());
    }

    public Slice<AlarmDto> findPresentationActiveAlarm(Long userId, Pageable pageable) {
        Slice<Alarm> alarmSlice = alarmRepository.findPresentationByUser(userId, pageable);
        return new SliceImpl<>(alarmSlice.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarmSlice.hasNext());
    }

//    public void readAlarm(Long userId) {
//        userRepository.findById(userId)
//                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
//                .updateReadAlarm(Boolean.TRUE);
//    }

    public Boolean hasRead(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .getReadAlarm();
    }
}
