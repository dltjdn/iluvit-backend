package FIS.iLUVit.controller;

import FIS.iLUVit.dto.blackUser.BlockedReasonResponse;
import FIS.iLUVit.service.BlackUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "블랙 유저 API")
@RequestMapping("black-user")
public class BlackUserController {

    private final BlackUserService blackUserService;

    /**
     * 블랙 유저 사유 조회
     */
    @Operation(summary = "블랙유저 사유 조회", description = "이용제한 혹은 영구정지되어 아이러빗을 이용할 수 없는 유저의 경우 차단 정보를 조회합니다.")
    @GetMapping("{loginId}")
    public ResponseEntity<BlockedReasonResponse> getBlockedReason(@PathVariable("loginId") String loginId) {
        BlockedReasonResponse response = blackUserService.getBlockedReason(loginId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
