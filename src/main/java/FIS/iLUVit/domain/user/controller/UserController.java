package FIS.iLUVit.domain.user.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.user.dto.PasswordUpdateRequest;
import FIS.iLUVit.domain.tokenpair.dto.TokenRefreshRequest;
import FIS.iLUVit.domain.user.dto.UserBasicInfoResponse;
import FIS.iLUVit.domain.user.dto.VersionInfoResponse;
import FIS.iLUVit.domain.user.dto.LoginRequestDto;
import FIS.iLUVit.domain.user.dto.UserInfoResponse;
import FIS.iLUVit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 유저 상세 조회
     */
    @GetMapping("user")
    public ResponseEntity<UserBasicInfoResponse> getUserDetails(@Login Long id) {
        UserBasicInfoResponse userBasicInfoResponse = userService.findUserDetails(id);
        return ResponseEntity.ok(userBasicInfoResponse);
    }

    /**
    * 아이디 중복 조회
    */
    @GetMapping("check-loginid")
    public ResponseEntity<Void> checkLoginId(@RequestParam String loginId) {
        userService.checkLoginIdAvailability(loginId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
    * 닉네임 중복 조회
    */
    @GetMapping("check-nickname")
    public ResponseEntity<Void> checkNickname(@RequestParam String nickname) {
        userService.checkNicknameAvailability(nickname);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("password")
    public ResponseEntity<Void> updatePassword(@Login Long id, @Valid @RequestBody PasswordUpdateRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 유저 로그인
     */
    @PostMapping("login")
    public ResponseEntity<UserInfoResponse> login(@RequestBody LoginRequestDto request) {
        UserInfoResponse userInfoResponse = userService.login(request);
        return ResponseEntity.ok(userInfoResponse);
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("refresh")
    public ResponseEntity<UserInfoResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        UserInfoResponse userInfoResponse = userService.refreshAccessToken(request);
        return ResponseEntity.ok(userInfoResponse);
    }

    /**
     * 앱 버전 확인
     */
    @GetMapping("version")
    public ResponseEntity<VersionInfoResponse> getVersion() {
        String iosVersion = env.getProperty("version.ios");
        String aosVersion = env.getProperty("version.aos");
        VersionInfoResponse versionInfoResponse = new VersionInfoResponse(iosVersion, aosVersion);
        return ResponseEntity.ok(versionInfoResponse);
    }

}
