package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.dto.user.*;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import com.auth0.jwt.exceptions.JWTVerificationException;
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
     * 작성자: 이승범
     * 작성내용: 유저 상세 조회
     */
    @GetMapping("user")
    public UserResponse getUserDetails(@Login Long id) {
        return userService.findUserInfo(id);
    }

    /**
    * 작성자: 이승범
    * 작성내용: 아이디 중복 조회
    */
    @GetMapping("check-loginid")
    public void checkLoginId(@Valid @ModelAttribute CheckLoginIdRequest request) {
        userService.checkLoginId(request);
    }

    /**
    *   작성자: 이승범
    *   작성내용: 닉네임 중복 조회
    */
    @GetMapping("check-nickname")
    public void checkNickname(@Valid @ModelAttribute CheckNicknameRequest request) {
        userService.checkNickname(request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 비밀번호 변경
     */
    @PutMapping("password")
    public void updatePassword(@Login Long id, @Valid @RequestBody PasswordRequest request) {
        userService.updatePassword(id, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 유저 로그인
     */
    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 토큰 재발급
     */
    @PostMapping("refresh")
    public LoginResponse refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return userService.refresh(request);
    }

    /**
     *   작성자: 박찬희
     *   작성내용: ios 앱 버전 확인
     */
    @GetMapping("version/ios")
    public String iosVersion() {
        //return env.getProperty("version.ios");
        return "테스트";
    }


    /**
     *   작성자: 박찬희
     *   작성내용: aos 앱 버전 확인
     */
    @GetMapping("version/aos")
    public String aosVersion() {
        return env.getProperty("version.aos");
    }

}
