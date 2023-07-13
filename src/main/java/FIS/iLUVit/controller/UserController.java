package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.user.*;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


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
    public ResponseEntity<UserResponse> getUserDetails(@Login Long id) {
        UserResponse userResponse = userService.findUserDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    /**
    * 작성자: 이승범
    * 작성내용: 아이디 중복 조회
    */
    @GetMapping("check-loginid")
    public ResponseEntity<Void> checkLoginId(@Valid @ModelAttribute CheckLoginIdRequest request) {
        userService.checkLoginIdAvailability(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
    *   작성자: 이승범
    *   작성내용: 닉네임 중복 조회
    */
    @GetMapping("check-nickname")
    public ResponseEntity<Void> checkNickname(@Valid @ModelAttribute CheckNicknameRequest request) {
        userService.checkNicknameAvailability(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 작성자: 이승범
     * 작성내용: 비밀번호 변경
     */
    @PutMapping("password")
    public ResponseEntity<Void> updatePassword(@Login Long id, @Valid @RequestBody PasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 작성자: 이승범
     * 작성내용: 유저 로그인
     */
    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 토큰 재발급
     */
    @PostMapping("refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        LoginResponse loginResponse =  userService.refreshAccessToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);

    }

    /**
     *   작성자: 박찬희
     *   작성내용: ios 앱 버전 확인
     */
    @GetMapping("version/ios")
    public String iosVersion() {
        return env.getProperty("version.ios");
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
