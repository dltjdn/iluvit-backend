package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.controller.dto.FindPasswordRequest;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.service.AuthNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthNumberController {

    private final AuthNumberService authNumberService;

    /**
     * 작성날짜: 2022/05/24 10:39 AM
     * 작성자: 이승범
     * 작성내용: 회원가입 위한 인증번호 전송
     */
    @GetMapping("/authNumber/signup")
    public void sendAuthNumberForSignup(@RequestParam String phoneNumber) {
        authNumberService.sendAuthNumberForSignup(phoneNumber, AuthKind.signup);
    }

    /**
     * 작성날짜: 2022/05/25 10:45 AM
     * 작성자: 이승범
     * 작성내용: 로그인 아이디를 찾기위한 인증번호 전송
     */
    @GetMapping("/authNumber/loginId")
    public void sendAuthNumberForFindLoginId(@RequestParam String phoneNumber) {
        authNumberService.sendAuthNumberForFindLoginId(phoneNumber);
    }

    /**
     * 작성날짜: 2022/05/25 2:47 PM
     * 작성자: 이승범
     * 작성내용: 비밀번호 찾기 인증번호 전송
     */
    @GetMapping("/authNumber/password")
    public void sendAuthNumberForFindPassword(@RequestParam String loginId, @RequestParam String phoneNumber) {
        authNumberService.sendAuthNumberForFindPassword(loginId, phoneNumber);
    }

    /**
    *   작성날짜: 2022/06/20 11:08 AM
    *   작성자: 이승범
    *   작성내용: 핸드폰번호 변경을 위한 인증번호 전송
    */
    @GetMapping("/authNumber/phoneNumber")
    public void sendAuthNumberForUpdatePhoneNum(@RequestParam String phoneNumber) {
        authNumberService.sendAuthNumberForSignup(phoneNumber, AuthKind.updatePhoneNum);
    }

    /**
     * 작성날짜: 2022/05/24 3:24 PM
     * 작성자: 이승범
     * 작성내용: 인증번호를 통한 핸드폰 인증
     */
    @PostMapping("/authNumber")
    public void AuthenticateAuthNum(@RequestBody AuthenticateAuthNumRequest request) {
        authNumberService.authenticateAuthNum(request);
    }

    /**
     * 작성날짜: 2022/05/25 3:39 PM
     * 작성자: 이승범
     * 작성내용: 인증번호를 통한 로그인 아이디 찾기
     */
    @PostMapping("/findLoginId")
    public String findLoginId(@RequestBody AuthenticateAuthNumRequest request) {
        return authNumberService.findLoginId(request);
    }

    /**
    *   작성날짜: 2022/05/25 4:15 PM
    *   작성자: 이승범
    *   작성내용: 비밀번호 찾기 근데 이제 변경을 곁들인
    */
    @PostMapping("/findPassword")
    public void findPassword(@RequestBody FindPasswordRequest request) {
        authNumberService.changePassword(request);
    }
}
