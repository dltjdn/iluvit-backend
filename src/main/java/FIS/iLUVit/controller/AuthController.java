package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.auth.AuthRequestDto;
import FIS.iLUVit.dto.auth.AuthLoginIdDto;
import FIS.iLUVit.dto.auth.AuthFindPasswordDto;
import FIS.iLUVit.service.AuthService;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok().build();
    }

    /**
     * (아이디찾기) 인증번호 받기
     */
    @GetMapping("loginid")
    public ResponseEntity<Void> getAuthNumForFindLoginId(@RequestParam String phoneNumber) {
        authService.sendAuthNumForFindLoginId(phoneNumber);
        return ResponseEntity.ok().build();
    }

    /**
     * (비밀번호찾기) 인증번호 받기
     */
    @GetMapping("password")
    public ResponseEntity<Void> getAuthNumForFindPwd(@RequestParam String loginId, @RequestParam String phoneNumber) {
        authService.sendAuthNumberForFindPassword(loginId, phoneNumber);
        return ResponseEntity.ok().build();
    }

    /**
     * (핸드폰 번호 변경) 인증번호 받기
     */
    @GetMapping("phonenumber")
    public ResponseEntity<Void> getAuthNumForUpdatePhoneNum(@Login Long userId, @RequestParam String phoneNumber) {
        authService.sendAuthNumForChangePhone(userId, phoneNumber);
        return ResponseEntity.ok().build();
    }

    /**
     * (회원가입, 비밀번호 찾기) 인증번호 인증
     */
    @PostMapping("")
    public void authenticateAuthNum(@RequestBody AuthRequestDto request) {
        authService.authenticateAuthNum(request);
    }

    /**
     * (핸드폰번호 변경) 인증번호 인증
     */
    @PostMapping("phonenum")
    public void authenticateAuthNumForChangingPhoneNum(@Login Long userId, @RequestBody AuthRequestDto request) {
        authService.authenticateAuthNumForChangingPhoneNum(userId, request);
    }

    /**
     * (아이디찾기) 인증번호 인증 후 유저 아이디 반환
     */
    @PostMapping("loginid")
    public ResponseEntity<AuthLoginIdDto> authenticateAuthNumForFindLoginId(@RequestBody AuthRequestDto authRequestDto) {
        AuthLoginIdDto authLoginIdDto = authService.authenticateAuthNumForFindLoginId(authRequestDto);
        return ResponseEntity.ok(authLoginIdDto);
    }

    /**
     * (비밀번호 변경용 비밀번호찾기) 인증이 완료된 핸드폰번호인지 확인 후 비밀번호 변경
     */
    @PostMapping("password")
    public ResponseEntity<Void> authenticateAuthNumForChangePwd(@RequestBody @Valid AuthFindPasswordDto authFindPasswordDto) {
        authService.authenticateAuthNumForChangePwd(authFindPasswordDto);
        return ResponseEntity.ok().build();
    }

}
