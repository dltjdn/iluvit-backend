package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.AlarmDeleteDto;
import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final UserService userService;
    private final Environment env;

    @GetMapping("/readAlarm")
    public void readAlarm(@Login Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        userService.readAlarm(userId);
    }

    @GetMapping("/alarm-active")
    public Slice<AlarmDto> getActiveAlarm(@Login Long userId, Pageable pageable){
        return userService.findUserActiveAlarm(userId, pageable);
    }

    @GetMapping("/alarm-presentation")
    public Slice<AlarmDto> getPresentationAlarm(@Login Long userId, Pageable pageable){
        return userService.findPresentationActiveAlarm(userId, pageable);
    }

    @DeleteMapping("/alarm")
    public Integer deleteAlarm(@Login Long userId, @RequestBody AlarmDeleteDto request) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return userService.deleteUserAlarm(userId, request.getAlarmIds());
    }

    @GetMapping("/hasRead")
    public Boolean hasRead(@Login Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return userService.hasRead(userId);
    }
}
