package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.alarm.AlarmRequest;
import FIS.iLUVit.dto.alarm.AlarmDetailDto;
import FIS.iLUVit.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
     * 알림 목록 삭제
     */
    @DeleteMapping("")
    public Integer deleteAlarm(@Login Long userId, @RequestBody AlarmRequest request) {
        return alarmService.deleteSelectedAlarm(userId, request.getAlarmIds());
    }

    /**
     * 활동 알림 조회
     */
    @GetMapping("active")
    public Slice<AlarmDetailDto> getActiveAlarm(@Login Long userId, Pageable pageable){
        return alarmService.findActiveAlarmByUser(userId, pageable);
    }

    /**
     * 설명회 알림 조회
     */
    @GetMapping("presentation")
    public Slice<AlarmDetailDto> getPresentationAlarm(@Login Long userId, Pageable pageable){
        return alarmService.findPresentationActiveAlarmByUser(userId, pageable);
    }

    /**
     * 전체 알림 읽었는지 안 읽었는지 받아오기
     */
    @GetMapping("is-read")
    public Boolean hasRead(@Login Long userId){
        return alarmService.hasRead(userId);
    }

    /**
     * 전체 알림 읽었다고 처리하기
     */
    @GetMapping("read")
    public void readAlarm(@Login Long userId){
        alarmService.readAlarm(userId);
    }


    /**
     *   작성자: 이서우
     *   작성내용: 해당 유저의 알림 전체 삭제
     */
    @DeleteMapping("all")
    public void deleteAllAlarm(@Login Long userId) {
        alarmService.deleteAllAlarm(userId);

    }
}
