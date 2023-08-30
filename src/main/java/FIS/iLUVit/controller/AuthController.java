package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.auth.AuthNumRequest;
import FIS.iLUVit.dto.auth.FindPasswordRequest;
import FIS.iLUVit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증번호 관련 API")
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이승범
     * 작성내용: (회원가입) 인증번호 받기
     */
    @Operation(summary = "(회원가입) 인증번호 받기", description = "회원가입을 위한 인증번호를 전송합니다.")
    @GetMapping("signup")
    public void getAuthNumForSignup(@RequestParam String phoneNumber) {
        authService.sendAuthNumForSignup(phoneNumber);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (아이디찾기) 인증번호 받기
     */
    @Operation(summary = "(아이디찾기) 인증번호 받기", description = "로그인 아이디를 찾기 위한 인증번호를 전송합니다.")
    @GetMapping("loginid")
    public void getAuthNumForFindLoginId(@RequestParam String phoneNumber) {
        authService.sendAuthNumForFindLoginId(phoneNumber);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (비밀번호찾기) 인증번호 받기
     */
    @Operation(summary = "(비밀번호찾기) 인증번호 받기", description = "비밀번호를 찾기 위한 인증번호를 전송합니다.")
    @GetMapping("password")
    public void getAuthNumForFindPwd(@RequestParam String loginId, @RequestParam String phoneNumber) {
        authService.sendAuthNumberForFindPassword(loginId, phoneNumber);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (핸드폰 변경) 인증번호 받기
     */
    @Operation(summary = "(핸드폰 변경) 인증번호 받기", description = "핸드폰 번호 변경을 위한 인증번호를 전송합니다.")
    @GetMapping("phonenumber")
    public void getAuthNumForUpdatePhoneNum(@Login Long userId, @RequestParam String phoneNumber) {
        authService.sendAuthNumForChangePhone(userId, phoneNumber);
    }

    /**
     * (회원가입, 비밀번호 찾기) 인증번호 인증
     */
    @Operation(summary = "(회원가입, 비밀번호 찾기) 인증번호 인증", description = "인증번호를 통한 핸드폰 인증을 합니다 (회원가입, 비밀번호 찾기).")
    @PostMapping("")
    public void authenticateAuthNum(@RequestBody AuthNumRequest request) {
        authService.authenticateAuthNum(request);
    }

    /**
     * (핸드폰번호 변경) 인증번호 인증
     */
    @Operation(summary = "(핸드폰번호 변경) 인증번호 인증 ", description = "인증번호를 통한 핸드폰 인증을 합니다 (핸드폰번호 변경).")
    @PostMapping("phonenum")
    public void authenticateAuthNumForChangingPhoneNum(@Login Long userId, @RequestBody AuthNumRequest request) {
        authService.authenticateAuthNumForChangingPhoneNum(userId, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (아이디찾기) 인증번호 인증
     */
    @Operation(summary = "(아이디찾기) 인증번호 인증", description = "인증번호를 통해 로그인 아이디를 찾습니다.")
    @PostMapping("loginid")
    public String authenticateAuthNumForFindLoginId(@RequestBody AuthNumRequest request) {
        return authService.authenticateAuthNumForFindLoginId(request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (비밀번호 변경용 비밀번호찾기) 인증번호 인증
     */
    @Operation(summary = "(비밀번호 변경용 비밀번호찾기) 인증번호 인증", description = "(비밀번호 변경용 비밀번호찾기) 인증번호 인증.")
    @PostMapping("password")
    public void authenticateAuthNumForChangePwd(@RequestBody @Valid FindPasswordRequest request) {
        authService.authenticateAuthNumForChangePwd(request);
    }

}
