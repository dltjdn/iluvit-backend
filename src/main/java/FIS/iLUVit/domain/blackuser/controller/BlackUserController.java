package FIS.iLUVit.domain.blackuser.controller;

import FIS.iLUVit.domain.blackuser.dto.BlockedReasonResponse;
import FIS.iLUVit.domain.blackuser.service.BlackUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("black-user")
public class BlackUserController {

    private final BlackUserService blackUserService;

    /**
     * 차단 정보 조회
     */
    @GetMapping("{loginId}")
    public ResponseEntity<BlockedReasonResponse> getBlockedReason(@PathVariable("loginId") String loginId) {
        BlockedReasonResponse response = blackUserService.getBlockedReason(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}