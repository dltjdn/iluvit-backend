package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.alarm.*;
import FIS.iLUVit.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<Slice<AlarmDto>> getActiveAlarm(@Login Long userId, Pageable pageable){
        Slice<AlarmDto> alarmDetailDtos = alarmService.findActiveAlarmByUser(userId, pageable);
        return ResponseEntity.ok(alarmDetailDtos);
    }

    /**
     * 설명회 알림을 조회합니다
     */
    @GetMapping("presentation")
    public ResponseEntity<Slice<AlarmDto>> getPresentationAlarm(@Login Long userId, Pageable pageable){
        Slice<AlarmDto> alarmDetailDtos = alarmService.findPresentationActiveAlarmByUser(userId, pageable);
        return ResponseEntity.ok(alarmDetailDtos);
    }

    /**
     * 전체 알림 읽음으로 업데이트
     */
    @PatchMapping("read")
    public ResponseEntity<Void> readAlarm(@Login Long userId) {
        alarmService.readAlarm(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 전체 알림 읽었는지 안 읽었는지 여부를 조회합니다
     */
    @GetMapping("is-read")
    public ResponseEntity<AlarmReadDto> hasRead(@Login Long userId){
        AlarmReadDto alarmReadDto = alarmService.hasRead(userId);
        return ResponseEntity.ok(alarmReadDto);
    }

    /**
     * 선택한 알림들을 삭제합니다
     */
    @DeleteMapping("")
    public ResponseEntity<Void> deleteAlarm(@Login Long userId, @RequestBody AlarmDeleteDto alarmDeleteDto) {
        alarmService.deleteSelectedAlarm(userId, alarmDeleteDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 모든 알림을 삭제합니다
     */
    @DeleteMapping("all")
    public ResponseEntity<Void> deleteAllAlarm(@Login Long userId) {
        alarmService.deleteAllAlarm(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
