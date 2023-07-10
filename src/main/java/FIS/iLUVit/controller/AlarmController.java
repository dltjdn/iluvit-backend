package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.dto.alarm.*;
import FIS.iLUVit.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("alarm")
public class AlarmController {

    private final AlarmService alarmService;

    /**
     * COMMON
     */

    /**
     * 활동 알림을 조회합니다
     */
    @GetMapping("active")
    public ResponseEntity<Slice<AlarmDetailDto>> getActiveAlarm(@Login Long userId, Pageable pageable){
        Slice<Alarm> alarms = alarmService.findActiveAlarmByUser(userId, pageable);

        SliceImpl<AlarmDetailDto> alarmDetailDtos = new SliceImpl<>(alarms.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarms.hasNext());

        return ResponseEntity.ok(alarmDetailDtos);
    }

    /**
     * 설명회 알림을 조회합니다
     */
    @GetMapping("presentation")
    public ResponseEntity<Slice<AlarmDetailDto>> getPresentationAlarm(@Login Long userId, Pageable pageable){
        Slice<Alarm> alarms = alarmService.findPresentationActiveAlarmByUser(userId, pageable);


        SliceImpl<AlarmDetailDto> alarmDetailDtos = new SliceImpl<>(alarms.stream()
                .map(Alarm::exportAlarm)
                .collect(Collectors.toList()),
                pageable, alarms.hasNext());

        return ResponseEntity.ok(alarmDetailDtos);
    }

    /**
     * 전체 알림 읽음으로 업데이트
     */
    @PatchMapping("read")
    public ResponseEntity<Void> readAlarm(@Login Long userId){
        alarmService.readAlarm(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 알림 읽었는지 안 읽었는지 여부를 조회합니다
     */
    @GetMapping("is-read")
    public ResponseEntity<Boolean> hasRead(@Login Long userId){
        Boolean hasRead = alarmService.hasRead(userId);
        return ResponseEntity.ok(hasRead);
    }

    /**
     * 선택한 알림들을 삭제합니다
     */
    @DeleteMapping("")
    public ResponseEntity<Void> deleteAlarm(@Login Long userId, @RequestBody AlarmRequest request) {
        alarmService.deleteSelectedAlarm(userId, request.getAlarmIds());
        return ResponseEntity.noContent().build();
    }

    /**
     * 모든 알림을 삭제합니다
     */
    @DeleteMapping("all")
    public ResponseEntity<Void> deleteAllAlarm(@Login Long userId) {
        alarmService.deleteAllAlarm(userId);
        return ResponseEntity.noContent().build();

    }
}
