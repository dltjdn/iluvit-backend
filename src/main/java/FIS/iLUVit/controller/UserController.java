package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.user.*;
import FIS.iLUVit.security.LoginRequest;
import FIS.iLUVit.security.LoginResponse;
import FIS.iLUVit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "유저 API")
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
    @Operation(summary = "유저 상세 조회", description = "유저의 기본정보를 조회합니다.")
    @GetMapping("user")
    public UserResponse getUserDetails(@Login Long id) {
        return userService.findUserDetails(id);
    }

    /**
    * 작성자: 이승범
    * 작성내용: 아이디 중복 조회
    */
    @Operation(summary = "아이디 중복 조회", description = "회원가입 시 이미 존재하는 아이디인지 확인합니다.")
    @GetMapping("check-loginid")
    public void checkLoginId(@Valid @ModelAttribute CheckLoginIdRequest request) {
        userService.checkLoginIdAvailability(request);
    }

    /**
    *   작성자: 이승범
    *   작성내용: 닉네임 중복 조회
    */
    @Operation(summary = "닉네임 중복 조회", description = "회원가입 시 이미 존재하는 별명인지 확인합니다.")
    @GetMapping("check-nickname")
    public void checkNickname(@Valid @ModelAttribute CheckNicknameRequest request) {
        userService.checkNicknameAvailability(request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경", description = "유저의 비밀번호를 변경합니다.")
    @PutMapping("password")
    public void updatePassword(@Login Long id, @Valid @RequestBody PasswordRequest request) {
        userService.changePassword(id, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 유저 로그인
     */
    @Operation(summary = "로그인", description = "loginId와 password를 받아 로그인 합니다.")
    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 토큰 재발급
     */
    @Operation(summary = "토큰 재발급", description = "refreshToken으로 accessToken을 재발급합니다.")
    @PostMapping("refresh")
    public LoginResponse refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return userService.refreshAccessToken(request);
    }

    /**
     *   작성자: 박찬희
     *   작성내용: ios 앱 버전 확인
     */
    @Operation(summary = "ios 앱 버전 확인", description = "ios 앱 버전을 조회합니다.")
    @GetMapping("version/ios")
    public String iosVersion() {
        return env.getProperty("version.ios");
    }


    /**
     *   작성자: 박찬희
     *   작성내용: aos 앱 버전 확인
     */
    @Operation(summary = "aos 앱 버전 확인", description = "aos 앱 버전을 조회합니다.")
    @GetMapping("version/aos")
    public String aosVersion() {
        return env.getProperty("version.aos");
    }

}
