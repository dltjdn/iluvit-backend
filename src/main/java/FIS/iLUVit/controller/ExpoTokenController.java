package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.ExpoTokenRequest;
import FIS.iLUVit.service.ExpoTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ExpoTokenController {

    private final ExpoTokenService expoTokenService;

    /**
     * 작성자: 이창윤
     * 앱 최초 접속 시 푸쉬 알림을 위한 [Token]을 받아야 합니다.
     */
    @PostMapping("/expoTokens")
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@Login Long userId,
            @RequestBody ExpoTokenRequest request) {
        return expoTokenService.saveToken(userId, request);
    }

    /**
     * 작성자: 이창윤
     * 푸쉬 알림 비동의하면 [Token]을 삭제합니다.
     */
    @DeleteMapping("/expoTokens")
    @ResponseStatus(HttpStatus.OK)
    public void remove(@Login Long userId,
                     @RequestBody ExpoTokenRequest request) {
        expoTokenService.deleteToken(userId, request);
    }

}
