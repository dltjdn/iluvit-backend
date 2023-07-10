package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.auth.AuthNumRequest;
import FIS.iLUVit.dto.auth.FindPasswordRequest;
import FIS.iLUVit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * (회원가입) 인증번호 받기
     */
    @GetMapping("signup")
    public ResponseEntity<Void>  getAuthNumForSignup(@RequestParam String phoneNumber) {
        authService.sendAuthNumForSignup(phoneNumber);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * (아이디찾기) 인증번호 받기
     */
    @GetMapping("loginid")
    public ResponseEntity<Void> getAuthNumForFindLoginId(@RequestParam String phoneNumber) {
        authService.sendAuthNumForFindLoginId(phoneNumber);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * (비밀번호찾기) 인증번호 받기
     */
    @GetMapping("password")
    public ResponseEntity<Void> getAuthNumForFindPwd(@RequestParam String loginId, @RequestParam String phoneNumber) {
        authService.sendAuthNumberForFindPassword(loginId, phoneNumber);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * (핸드폰 번호 변경) 인증번호 받기
     */
    @GetMapping("phonenumber")
    public ResponseEntity<Void> getAuthNumForUpdatePhoneNum(@Login Long userId, @RequestParam String phoneNumber) {
        authService.sendAuthNumForChangePhone(userId, phoneNumber);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * (회원가입, 비밀번호찾기, 핸드폰번호 변경) 인증번호 인증
     */
    @PostMapping("")
    public ResponseEntity<Void> authenticateAuthNum(@Login Long userId, @RequestBody AuthNumRequest request) {
        authService.authenticateAuthNum(userId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * (아이디찾기) 인증번호 인증 후 유저 아이디 반환
     */
    @PostMapping("loginid")
    public ResponseEntity<String> authenticateAuthNumForFindLoginId(@RequestBody AuthNumRequest request) {
        String blindLoginId = authService.authenticateAuthNumForFindLoginId(request);
        return ResponseEntity.status(HttpStatus.OK).body(blindLoginId);
    }

    /**
     * (비밀번호 변경용 비밀번호찾기) 인증번호 인증 후 비밀번호 변경
     */
    @PostMapping("password")
    public ResponseEntity<Void> authenticateAuthNumForChangePwd(@RequestBody @Valid FindPasswordRequest request) {
        authService.authenticateAuthNumForChangePwd(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
