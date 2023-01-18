package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.AlarmDeleteDto;
import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.service.AlarmService;
import FIS.iLUVit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("alarm")
public class AlarmController {

    private final AlarmService alarmService;


//    public void readAlarm(@Login Long userId){
//        if(userId == null)
//            throw new UserException(UserErrorResult.NOT_LOGIN);
//        alarmService.readAlarm(userId);
//    }

    @GetMapping("")
    public Slice<AlarmDto> getActiveAlarm(@Login Long userId, Pageable pageable){
        return alarmService.findUserActiveAlarm(userId, pageable);
    }

    @GetMapping("presentation")
    public Slice<AlarmDto> getPresentationAlarm(@Login Long userId, Pageable pageable){
        return alarmService.findPresentationActiveAlarm(userId, pageable);
    }

    @DeleteMapping("")
    public Integer deleteAlarm(@Login Long userId, @RequestBody AlarmDeleteDto request) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return alarmService.deleteUserAlarm(userId, request.getAlarmIds());
    }

    @GetMapping("is-read")
    public Boolean hasRead(@Login Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return alarmService.hasRead(userId);
    }
}
