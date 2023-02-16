package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.auth.AuthNumRequest;
import FIS.iLUVit.dto.auth.FindPasswordRequest;
import FIS.iLUVit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
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
    @GetMapping("signup")
    public void sendAuthNumberForSignup(@RequestParam String phoneNumber) {
        authService.sendAuthNumberForSignup(phoneNumber);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (아이디찾기) 인증번호 받기
     */
    @GetMapping("loginid")
    public void sendAuthNumForFindLoginId(@RequestParam String phoneNumber) {
        authService.sendAuthNumberForFindLoginId(phoneNumber);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (비밀번호찾기) 인증번호 받기
     */
    @GetMapping("password")
    public void sendAuthNumForFindPwd(@RequestParam String loginId, @RequestParam String phoneNumber) {
        authService.sendAuthNumberForFindPassword(loginId, phoneNumber);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (핸드폰 변경) 인증번호 받기
     */
    @GetMapping("phonenumber")
    public void sendAuthNumForUpdatePhoneNum(@Login Long userId, @RequestParam String phoneNumber) {
        authService.sendAuthNumberForChangePhone(userId, phoneNumber);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (회원가입, 비밀번호 찾기, 핸드폰번호 변경) 인증번호 인증
     */
    @PostMapping("")
    public void authenticateAuthNum(@Login Long userId, @RequestBody AuthNumRequest request) {
        authService.authenticateAuthNum(userId, request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (아이디찾기) 인증번호 인증
     */
    @PostMapping("loginid")
    public String authenticateAuthNumForFindLoginId(@RequestBody AuthNumRequest request) {
        return authService.findLoginId(request);
    }

    /**
     * 작성자: 이승범
     * 작성내용: (비밀번호 변경용 비밀번호찾기) 인증번호 인증
     */
    @PostMapping("password")
    public void authenticateAuthNumForChangePwd(@RequestBody @Valid FindPasswordRequest request) {
        authService.changePassword(request);
    }

}
