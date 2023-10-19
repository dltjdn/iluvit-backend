package FIS.iLUVit.domain.expotoken.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.expotoken.dto.ExpoTokenDeviceIdRequest;
import FIS.iLUVit.domain.expotoken.dto.ExpoTokenFindOneResponse;
import FIS.iLUVit.domain.expotoken.dto.ExpoTokenSaveRequest;
import FIS.iLUVit.domain.expotoken.service.ExpoTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * expoToken 등록
     */
    @PostMapping("")
    public ResponseEntity<Long> createExpoToken(@Login Long userId, @RequestBody @Valid ExpoTokenSaveRequest request) {
        Long response = expoTokenService.saveToken(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * expoToken 조회
     */
    @GetMapping("")
    public ResponseEntity<ExpoTokenFindOneResponse> getExpoToken(@Login Long userId, HttpServletRequest request) {
        String expoToken = request.getHeader("ExpoToken");
        ExpoTokenFindOneResponse response = expoTokenService.findExpoTokenByUser(userId, expoToken);
        return ResponseEntity.ok().body(response);
    }

    /**
     * expoToken 삭제
     */
    @DeleteMapping("")
    public ResponseEntity<Void> deleteExpoToken(@Login Long userId, HttpServletRequest request) {
        String expoToken = request.getHeader("ExpoToken");
        expoTokenService.deleteExpoTokenByUser(userId, expoToken);
        return ResponseEntity.noContent().build();
    }

    /**
     * expoToken 비활성화 ( 회원가입 한 유저가 앱 삭제 후 재설치 할 때 사용 )
     */
    @PatchMapping("deactivate")
    public ResponseEntity<Void> deactivateToken( @RequestBody @Valid ExpoTokenDeviceIdRequest expoTokenDeviceIdRequest){
        expoTokenService.deactivateExpoToken(expoTokenDeviceIdRequest);
        return ResponseEntity.noContent().build();
    }


    /**
     * 비활성화된 expoToken 삭제
     */
    @DeleteMapping("deactivate")
    public ResponseEntity<Void> deleteDeactivatedToken(HttpServletRequest request){
        String deviceId = request.getHeader("DeviceId");
        expoTokenService.deleteDeactivatedExpoToken(deviceId);
        return ResponseEntity.noContent().build();
    }

}
