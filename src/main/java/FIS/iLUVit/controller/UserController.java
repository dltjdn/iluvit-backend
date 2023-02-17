package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.user.*;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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
     * COMMON
     */

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 사용자 기본정보(id, nickname, auth) 조회
     */
    @GetMapping("user")
    public UserResponse findUserInfo(@Login Long id) {
        return userService.findUserInfo(id);
    }

    /**
    *   작성날짜: 2022/07/29 5:04 PM
    *   작성자: 이승범
    *   작성내용: 아이디 중복 확인
    */
    @GetMapping("check-loginid")
    public void checkLoginId(@Valid @ModelAttribute CheckLoginIdRequest request) {
        userService.checkLoginId(request);
    }

    /**
    *   작성날짜: 2022/07/29 5:04 PM
    *   작성자: 이승범
    *   작성내용: 닉네임 중복 확인
    */
    @GetMapping("check-nickname")
    public void checkNickname(@Valid @ModelAttribute CheckNicknameRequest request) {
        userService.checkNickname(request);
    }

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 비밀번호 변경
     */
    @PutMapping("password")
    public void updatePassword(@Login Long id, @Valid @RequestBody PasswordRequest request) {
        userService.updatePassword(id, request);
    }

    /**
     *   작성날짜: 2022/07/29 01:32 AM
     *   작성자: 이승범
     *   작성내용: login기능 security filter에서 옮김
     */
    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /**
     *   작성날짜: 2022/07/29 01:32 AM
     *   작성자: 이승범
     *   작성내용: refreshToken으로 AccessToken발급
     */
    @PostMapping("refresh")
    public LoginResponse refresh(@Valid @RequestBody TokenRefreshRequest request) throws IOException {
        return userService.refresh(request);
    }

    /**
     *   작성날짜: 2022/08/12 10:39 AM
     *   작성자: 이승범
     *   작성내용: healthCheck test
     */
    @GetMapping("profile")
    public String profile() {
        return env.getProperty("spring.profiles.active");
    }


    /**
     *   작성날짜: 2023/02/16 5:52 PM
     *   작성자: 박찬희
     *   작성내용: ios 앱 버전 확인용 api
     */
    @GetMapping("ios-version")
    public String iosVersion() {
        return env.getProperty("version.ios");
    }


    /**
     *   작성날짜: 2023/02/16 5:52 PM
     *   작성자: 박찬희
     *   작성내용: aos 앱 버전 확인용 api
     */
    @GetMapping("aos-version")
    public String aosVersion() {
        return env.getProperty("version.aos");
    }
}
