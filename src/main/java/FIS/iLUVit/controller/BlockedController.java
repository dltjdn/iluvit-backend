package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.BlockedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저 차단 API")
@RequestMapping("user-block")
public class BlockedController {

    private final BlockedService blockedService;
    @Operation(summary = "(유저가) 유저를 차단합니다", description = "유저가 보고 싶지 않은 유저를 차단합니다.")
    @PostMapping("{blockedUserId}")
    public ResponseEntity<Void> blockedUser(@Login Long blockingUserId, @PathVariable("blockedUserId") Long blockedUserId) {
        blockedService.createBlocked(blockingUserId, blockedUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
