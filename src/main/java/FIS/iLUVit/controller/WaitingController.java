package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional
@RequestMapping("waiting")
public class WaitingController {

    private final WaitingService waitingService;

    /**
     * PARENT
     */

    /**
     * 설명회 회차에 대기를 신청합니다
     */
    @PostMapping("{ptDateId}")
    public ResponseEntity<Void> registerWaiting(@Login Long userId, @PathVariable("ptDateId") Long ptDateId){
        waitingService.waitingParticipation(userId, ptDateId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 설명회 회차에 대기 신청을 취소합니다
     */
    @DeleteMapping("{waitingId}")
    public ResponseEntity<Void> cancelWaiting(@Login Long userId, @PathVariable("waitingId") Long waitingId) {
        waitingService.cancelParticipation(userId, waitingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}