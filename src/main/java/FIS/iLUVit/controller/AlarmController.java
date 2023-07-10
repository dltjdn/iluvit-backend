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
     작성날짜: 2023/07/07 7:35 PM
     작성자: 이서우
     작성내용: 활동 알림을 조회합니다
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
     작성날짜: 2023/07/07 7:36 PM
     작성자: 이서우
     작성내용: 설명회 알림을 조회합니다
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
     작성날짜: 2023/07/07 7:49 PM
     작성자: 이서우
     작성내용: 전체 알림 읽었다고 처리하기
     */
    @GetMapping("read")
    public ResponseEntity<Void> readAlarm(@Login Long userId){
        alarmService.readAlarm(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     작성날짜: 2023/07/07 7:57 PM
     작성자: 이서우
     작성내용: 전체 알림 읽었는지 안 읽었는지 여부를 조회합니다
     */
    @GetMapping("is-read")
    public ResponseEntity<Boolean> hasRead(@Login Long userId){
        Boolean hasRead = alarmService.hasRead(userId);
        return ResponseEntity.ok(hasRead);
    }


    /**
     작성날짜: 2023/07/07 7:25 PM
     작성자: 이서우
     작성내용: 선택한 알림들을 삭제합니다
     */
    @DeleteMapping("")
    public ResponseEntity<Void> deleteAlarm(@Login Long userId, @RequestBody AlarmRequest request) {
        alarmService.deleteSelectedAlarm(userId, request.getAlarmIds());
        return ResponseEntity.noContent().build();
    }

    /**
     작성날짜: 2023/07/07 7:26 PM
     작성자: 이서우
     작성내용: 모든 알림을 삭제합니다
     */
    @DeleteMapping("all")
    public ResponseEntity<Void> deleteAllAlarm(@Login Long userId) {
        alarmService.deleteAllAlarm(userId);
        return ResponseEntity.noContent().build();

    }
}
