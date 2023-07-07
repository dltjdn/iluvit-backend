package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.alarm.AlarmReadResponseDto;
import FIS.iLUVit.dto.alarm.AlarmResponseDto;
import FIS.iLUVit.dto.alarm.AlarmRequest;
import FIS.iLUVit.dto.alarm.AlarmDetailResponseDto;
import FIS.iLUVit.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
     작성날짜: 2023/07/07 7:35 PM
     작성자: 이서우
     작성내용: 활동 알림을 조회합니다
     */
    @GetMapping("active")
    public AlarmDetailResponseDto getActiveAlarm(@Login Long userId, Pageable pageable){
        return alarmService.findActiveAlarmByUser(userId, pageable);
    }

    /**
     작성날짜: 2023/07/07 7:36 PM
     작성자: 이서우
     작성내용: 설명회 알림을 조회합니다
     */
    @GetMapping("presentation")
    public AlarmDetailResponseDto getPresentationAlarm(@Login Long userId, Pageable pageable){
        return alarmService.findPresentationActiveAlarmByUser(userId, pageable);
    }


    /**
     작성날짜: 2023/07/07 7:49 PM
     작성자: 이서우
     작성내용: 전체 알림 읽었다고 처리하기
     */
    @GetMapping("read")
    public AlarmResponseDto readAlarm(@Login Long userId){
        return alarmService.readAlarm(userId);
    }

    /**
     * 전체 알림 읽었는지 안 읽었는지 받아오기
     */
    @GetMapping("is-read")
    public AlarmReadResponseDto hasRead(@Login Long userId){
        return alarmService.hasRead(userId);
    }


    /**
     작성날짜: 2023/07/07 7:25 PM
     작성자: 이서우
     작성내용: 선택한 알림들을 삭제합니다
     */
    @DeleteMapping("")
    public AlarmResponseDto deleteAlarm(@Login Long userId, @RequestBody AlarmRequest request) {
        return alarmService.deleteSelectedAlarm(userId, request.getAlarmIds());
    }

    /**
     작성날짜: 2023/07/07 7:26 PM
     작성자: 이서우
     작성내용: 모든 알림을 삭제합니다
     */
    @DeleteMapping("all")
    public AlarmResponseDto deleteAllAlarm(@Login Long userId) {
        return alarmService.deleteAllAlarm(userId);

    }
}
