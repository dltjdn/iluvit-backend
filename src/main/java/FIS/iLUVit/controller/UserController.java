package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.PatchPasswordRequest;
import FIS.iLUVit.filter.LoginResponse;
import FIS.iLUVit.service.SignService;
import FIS.iLUVit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public void updatePassword(@Login Long id, PatchPasswordRequest request) {
        userService.updatePassword(id, request);
    }

    @GetMapping("/authPhone")
    public void sendAuthNumber(@RequestParam String requestNumber) {
        signService.sendAuthNumber(requestNumber);
    }

    @PostMapping("/signup")
    public void signUp() {

    }
}
