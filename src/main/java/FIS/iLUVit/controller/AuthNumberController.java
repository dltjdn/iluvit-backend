package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.controller.dto.FindPasswordRequest;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.service.AuthNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("authNumber")
public class AuthNumberController {

    private final AuthNumberService authNumberService;

    /**
     * 작성날짜: 2022/05/24 10:39 AM
     * 작성자: 이승범
     * 작성내용: 회원가입 위한 인증번호 전송
     */
    @GetMapping("signup")
    public void sendAuthNumberForSignup(@RequestParam String phoneNumber) {
        authNumberService.sendAuthNumberForSignup(phoneNumber);
    }

    /**
     * 작성날짜: 2022/05/25 10:45 AM
     * 작성자: 이승범
     * 작성내용: 로그인 아이디를 찾기위한 인증번호 전송
     */
    @GetMapping("loginId")
    public void sendAuthNumberForFindLoginId(@RequestParam String phoneNumber) {
        authNumberService.sendAuthNumberForFindLoginId(phoneNumber);
    }

    /**
     * 작성날짜: 2022/05/25 2:47 PM
     * 작성자: 이승범
     * 작성내용: 비밀번호 찾기 인증번호 전송
     */
    @GetMapping("password")
    public void sendAuthNumberForFindPassword(@RequestParam String loginId, @RequestParam String phoneNumber) {
        authNumberService.sendAuthNumberForFindPassword(loginId, phoneNumber);
    }

    /**
    *   작성날짜: 2022/06/20 11:08 AM
    *   작성자: 이승범
    *   작성내용: 핸드폰번호 변경을 위한 인증번호 전송
    */
    @GetMapping("phoneNumber")
    public void sendAuthNumberForUpdatePhoneNum(@Login Long userId, @RequestParam String phoneNumber) {
        authNumberService.sendAuthNumberForChangePhone(userId, phoneNumber);
    }

    /**
     * 작성날짜: 2022/05/24 3:24 PM
     * 작성자: 이승범
     * 작성내용: 인증번호를 통한 핸드폰 인증 (회원가입, 비밀번호 찾기, 핸드폰번호 변경)
     */
    @PostMapping("")
    public void authenticateAuthNum(@Login Long userId, @RequestBody AuthenticateAuthNumRequest request) {
        authNumberService.authenticateAuthNum(userId, request);
    }

    /**
     * 작성날짜: 2022/05/25 3:39 PM
     * 작성자: 이승범
     * 작성내용: 인증번호를 통한 로그인 아이디 찾기
     */
    @PostMapping("findLoginId")
    public String findLoginId(@RequestBody AuthenticateAuthNumRequest request) {
        return authNumberService.findLoginId(request);
    }

    /**
    *   작성날짜: 2022/05/25 4:15 PM
    *   작성자: 이승범
    *   작성내용: 비밀번호 찾기 근데 이제 변경을 곁들인
    */
    @PostMapping("findPassword")
    public void findPassword(@RequestBody @Valid FindPasswordRequest request) {
        authNumberService.changePassword(request);
    }
}
