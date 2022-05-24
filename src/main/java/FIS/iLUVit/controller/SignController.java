package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    /**
     * 작성날짜: 2022/05/24 10:39 AM
     * 작성자: 이승범
     * 작성내용: 인증번호 전송
     */
    @GetMapping("/authNumber")
    public void sendAuthNumber(@RequestParam String phoneNumber, @RequestParam AuthKind authKind) {
        signService.sendAuthNumber(phoneNumber, authKind);
    }

    /**
     * 작성날짜: 2022/05/24 3:24 PM
     * 작성자: 이승범
     * 작성내용: 인증번호를 통한 핸드폰 인증
     */
    @PostMapping("/authNumber")
    public void AuthenticateAuthNum(@RequestBody AuthenticateAuthNumRequest request) {
        signService.authenticateAuthNum(request);
    }

    @GetMapping("/findPassword")
    public String findLoginId(@RequestParam AuthenticateAuthNumRequest request) {
        signService.findLoginId(request);
    }
}
