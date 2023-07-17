package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.user.*;
import FIS.iLUVit.security.LoginRequestDto;
import FIS.iLUVit.security.UserDto;
import FIS.iLUVit.service.UserService;
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
    public ResponseEntity<UserBasicInfoDto> getUserDetails(@Login Long id) {
        UserBasicInfoDto userBasicInfoDto = userService.findUserDetails(id);
        return ResponseEntity.ok(userBasicInfoDto);
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
    public ResponseEntity<Void> updatePassword(@Login Long id, @Valid @RequestBody PasswordUpdateDto request) {
        userService.changePassword(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 유저 로그인
     */
    @PostMapping("login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) {
        UserDto userDto = userService.login(request);
        return ResponseEntity.ok(userDto);
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("refresh")
    public ResponseEntity<UserDto> refreshToken(@Valid @RequestBody TokenRefreshRequestDto request) {
        UserDto userDto = userService.refreshAccessToken(request);
        return ResponseEntity.ok(userDto);
    }

    /**
     * 앱 버전 확인
     */
    @GetMapping("version")
    public ResponseEntity<VersionInfoDto> getVersion() {
        String iosVersion = env.getProperty("version.ios");
        String aosVersion = env.getProperty("version.aos");

        VersionInfoDto versionInfoDto = new VersionInfoDto(iosVersion, aosVersion);

        return ResponseEntity.ok(versionInfoDto);
    }

}
