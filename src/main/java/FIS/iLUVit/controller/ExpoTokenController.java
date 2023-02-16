package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.expoToken.ExpoTokenDto;
import FIS.iLUVit.dto.expoToken.ExpoTokenRequest;
import FIS.iLUVit.service.ExpoTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("expo-tokens")
public class ExpoTokenController {

    private final ExpoTokenService expoTokenService;

    /**
     * COMMON
     */

    /**
     * 작성자: 이창윤
     * 작성내용: expoToken 등록
     * 앱 최초 접속 시 푸쉬 알림을 위한 [Token]을 받아야 합니다.
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createExpoToken(@Login Long userId,
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
     * 작성내용: expoToken 조회
     * 비고: 현재 알림 수신 OX 상태 들어있음, O --> True, X --> False 로 응답
     */
    @GetMapping("{token}")
    public ExpoTokenDto getExpoToken(@Login Long userId,
                                 @PathVariable String token) {
        return expoTokenService.findById(userId, token);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: expoToken 삭제
     */
    @DeleteMapping("{token}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteExpoToken(@Login Long userId,
                                  @PathVariable String token) {
        expoTokenService.deleteById(userId, token);
    }

}
