package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.ExpoTokenInfo;
import FIS.iLUVit.controller.dto.ExpoTokenRequest;
import FIS.iLUVit.service.ExpoTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
            @RequestBody @Valid ExpoTokenRequest request) {
        return expoTokenService.saveToken(userId, request);
    }

//    /**
//     * 작성자: 이창윤
//     * 푸쉬 알림 동의, 비동의 체크?
//     */
//    @PostMapping("/expoTokens/status")
//    @ResponseStatus(HttpStatus.OK)
//    public void modifyStatus(@Login Long userId,
//                       @RequestBody @Valid ExpoTokenRequest request) {
//        expoTokenService.modifyAcceptStatus(userId, request);
//    }

    /**
     * 작성자: 이창윤
     * 엑스포 토큰 정보 조회
     * 현재 알림 수신 OX 상태 들어있음
     * O --> True, X --> False 로 응답
     */
    @GetMapping("/expoTokens/{token}")
    public ExpoTokenInfo findById(@Login Long userId,
                                  @PathVariable String token) {
        return expoTokenService.findById(userId, token);
    }

    /**
     * 작성자: 이창윤
     * 엑스포 토큰 삭제
     * 유저 로그아웃 시 토큰 삭제하기
     */
    @DeleteMapping("/expoTokens/{token}")
    @ResponseStatus(HttpStatus.OK)
    public void remove(@Login Long userId,
                                  @PathVariable String token) {
        expoTokenService.deleteById(userId, token);
    }

}
