package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.BlockedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("user-block")
public class BlockedController {

    private final BlockedService blockedService;

    @PostMapping("{blockedUserId}")
    public ResponseEntity<Void> blockedUser(@Login Long blockingUserId, @PathVariable("blockedUserId") Long blockedUserId) {
        blockedService.createBlocked(blockingUserId, blockedUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
