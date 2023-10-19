package FIS.iLUVit.domain.user.controller;

import FIS.iLUVit.domain.user.dto.*;
import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.tokenpair.dto.TokenRefreshRequest;
import FIS.iLUVit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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
     * 유저 상세 조회
     */
    @GetMapping("user")
    public ResponseEntity<UserFindOneResponse> getUserDetails(@Login Long id) {
        UserFindOneResponse userBasicInfoResponse = userService.findUserDetails(id);
        return ResponseEntity.ok(userBasicInfoResponse);
    }

    /**
    * 아이디 중복 조회
    */
    @GetMapping("check-loginid")
    public ResponseEntity<Void> checkLoginId(@RequestBody UserCheckDuplicateLoginIdRequest request) {
        userService.checkLoginIdAvailability(request);
        return ResponseEntity.noContent().build();
    }

    /**
    * 닉네임 중복 조회
    */
    @GetMapping("check-nickname")
    public ResponseEntity<Void> checkNickname(@RequestBody UserCheckDuplicateNicknameRequest request) {
        userService.checkNicknameAvailability(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("password")
    public ResponseEntity<Void> updatePassword(@Login Long id, @Valid @RequestBody UserPasswordUpdateRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 유저 로그인
     */
    @PostMapping("login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse userLoginResponse = userService.login(request);
        return ResponseEntity.ok(userLoginResponse);
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("refresh")
    public ResponseEntity<UserLoginResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        UserLoginResponse userLoginResponse = userService.refreshAccessToken(request);
        return ResponseEntity.ok(userLoginResponse);
    }

    /**
     * AOS 버전 확인
     */
    @GetMapping("version/aos")
    public ResponseEntity<String> getAosVersion() {
        String aosVersion = env.getProperty("version.aos");
        return ResponseEntity.ok(aosVersion);
    }

    /**
     * IOS 버전 확인
     */
    @GetMapping("version/ios")
    public ResponseEntity<String> getIosVersion() {
        String iosVersion = env.getProperty("version.ios");
        return ResponseEntity.ok(iosVersion);
    }

}
