package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Environment env;

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
    *   작성날짜: 2022/07/29 5:04 PM
    *   작성자: 이승범
    *   작성내용: 로그인 중복 확인
    */
    @GetMapping("/loginid")
    public void checkLoginId(@Valid @ModelAttribute CheckLoginIdRequest request) {
        userService.checkLoginId(request.getLoginId());
    }

    /**
    *   작성날짜: 2022/07/29 5:04 PM
    *   작성자: 이승범
    *   작성내용: 닉네임 중복 확인
    */
    @GetMapping("/nickname")
    public void checkNickname(@RequestParam String nickname) {
        userService.checkNickname(nickname);
    }

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 비밀번호 변경
     */
    @PutMapping("/user/password")
    public void updatePassword(@Login Long id, @Valid @RequestBody UpdatePasswordRequest request) {
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
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return userService.deleteUserAlarm(userId, request.getAlarmIds());
    }

    /**
     *   작성날짜: 2022/07/29 01:32 AM
     *   작성자: 이승범
     *   작성내용: login기능 security filter에서 옮김
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /**
     *   작성날짜: 2022/07/29 01:32 AM
     *   작성자: 이승범
     *   작성내용: refreshToken으로 AccessToken발급
     */
    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody TokenRefreshRequest request) throws IOException {
        LoginResponse response = userService.refresh(request);
        if (response != null) {
            return response;
        } else {
            throw new JWTVerificationException("유효하지 않은 시도입니다.");
        }
    }

    /**
     *   작성날짜: 2022/08/12 10:39 AM
     *   작성자: 이승범
     *   작성내용: healthCheck test
     */
    @GetMapping("/profile")
    public String profile() {
        return env.getProperty("spring.profiles.active");
//        return Arrays.stream(env.getActiveProfiles())
//                .filter(str -> str.startsWith("http"))
//                .findFirst()
//                .orElse("");
    }

    @GetMapping("/readAlarm")
    public void readAlarm(@Login Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        userService.readAlarm(userId);
    }

    @GetMapping("/hasRead")
    public Boolean hasRead(@Login Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return userService.hasRead(userId);
    }

}
