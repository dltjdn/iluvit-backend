package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.AlarmDeleteDto;
import FIS.iLUVit.controller.dto.AlarmDto;
import FIS.iLUVit.controller.dto.UpdatePasswordRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 사용자 기본정보(id, nickname, auth)반환
     */
    @GetMapping("/user/info")
    public LoginResponse findUserInfo(@Login Long id) {
        return userService.findUserInfo(id);
    }

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 비밀번호 변경
     */
    @PutMapping("/user/password")
    public void updatePassword(@Login Long id, @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
    }

    @GetMapping("/alarm")
    public Slice<AlarmDto> getAlarm(@Login Long userId, Pageable pageable) {
        return userService.findUserAlarm(userId, pageable);
    }

    @DeleteMapping("/alarm")
    public Integer deleteAlarm(@Login Long userId, @RequestBody AlarmDeleteDto request) {
        return userService.deleteUserAlarm(userId, request.getAlarmId());
    }

    @PostMapping("/login")
    public void login() {

    }
}
