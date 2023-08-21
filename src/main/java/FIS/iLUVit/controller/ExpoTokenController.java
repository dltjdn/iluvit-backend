package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.expoToken.ExpoTokenDeviceIdRequest;
import FIS.iLUVit.dto.expoToken.ExpoTokenResponse;
import FIS.iLUVit.dto.expoToken.ExpoTokenSaveRequest;
import FIS.iLUVit.service.ExpoTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
     * 비고: 앱 최초 접속 시 푸쉬 알림을 위한 [Token]을 받아야 합니다.
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createExpoToken(@Login Long userId, @RequestBody @Valid ExpoTokenSaveRequest request) {
        return expoTokenService.saveToken(userId, request);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: expoToken 조회
     * 비고: 현재 알림 수신 OX 상태 들어있음, O --> True, X --> False 로 응답
     */
    @GetMapping("")
    public ExpoTokenResponse getExpoToken(@Login Long userId, HttpServletRequest request) {
        String expoToken = request.getHeader("ExpoToken");
        return expoTokenService.findExpoTokenByUser(userId, expoToken);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: expoToken 삭제
     */
    @DeleteMapping("")
    @ResponseStatus(HttpStatus.OK)
    public void deleteExpoToken(@Login Long userId, HttpServletRequest request) {
        String expoToken = request.getHeader("ExpoToken");
        expoTokenService.deleteExpoTokenByUser(userId, expoToken);
    }

    /**
     * expoToken 비활성화 ( 회원가입 한 유저가 앱 삭제 후 재설치 할 때 사용 )
     */
    @PatchMapping("deactivate")
    public void deactivateToken( @RequestBody @Valid ExpoTokenDeviceIdRequest expoTokenDeviceIdRequest){
        expoTokenService.deactivateExpoToken(expoTokenDeviceIdRequest);
    }


    /**
     * 비활성화 된 expoToken을 삭제한다
     */
    @DeleteMapping("deactivate")
    public void deleteDeactivatedToken(@RequestBody @Valid ExpoTokenDeviceIdRequest expoTokenDeviceIdRequest){
        expoTokenService.deleteDeactivatedExpoToken(expoTokenDeviceIdRequest);
    }

}
