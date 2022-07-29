package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.repository.TokenPairRepository;
import FIS.iLUVit.security.JwtUtils;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final TokenPairRepository tokenPairRepository;

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 사용자 기본정보(id, nickname, auth)반환
     */
    @GetMapping("/user/info")
    public UserInfoResponse findUserInfo(@Login Long id) {
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
        return userService.deleteUserAlarm(userId, request.getAlarmId());
    }

    /**
     *   작성날짜: 2022/07/29 01:32 AM
     *   작성자: 이승범
     *   작성내용: login기능 security filter에서 옮김
     */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /**
     *   작성날짜: 2022/07/29 01:32 AM
     *   작성자: 이승범
     *   작성내용: refreshToken으로 AccessToken발급
     */
    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return userService.refresh(request);
    }
}
