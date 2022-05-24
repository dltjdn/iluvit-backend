package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.AuthenticateAuthNumRequest;
import FIS.iLUVit.controller.dto.UpdatePasswordRequest;
import FIS.iLUVit.filter.LoginResponse;
import FIS.iLUVit.service.SignService;
import FIS.iLUVit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SignService signService;

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 사용자 기본정보(id, nickname, auth)반환
     */
    @GetMapping("/user/info")
    public LoginResponse findUserInfo(@Login Long id) {
        return userService.findUserInfo(id);
    }

    /**
     * 작성날짜: 2022/05/16 11:58 AM
     * 작성자: 이승범
     * 작성내용: 비밀번호 변경
     */
    @PatchMapping("/user/password")
    public void updatePassword(@Login Long id, UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
    }

    /**
    *   작성날짜: 2022/05/24 10:39 AM
    *   작성자: 이승범
    *   작성내용: 인증번호 전송
    */
    @GetMapping("/authNumber")
    public void sendAuthNumber(@RequestParam String phoneNumber) {
        signService.sendAuthNumber(phoneNumber);
    }

    @PostMapping("/authNumber")
    public void AuthenticateAuthNum(@RequestBody AuthenticateAuthNumRequest request) {
        signService.authenticateAuthNum(request);
    }

}
