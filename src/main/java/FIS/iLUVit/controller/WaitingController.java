package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.waiting.WaitingRegisterDto;
import FIS.iLUVit.service.WaitingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional
@Tag(name = "설명회 대기 API")
@RequestMapping("waiting")
public class WaitingController {

    private final WaitingService waitingService;

    /**
     * PARENT
     */

    /**
     * 작성자: 현승구
     * 작성내용: 설명회 대기 신청
     */
    @Operation(summary = "설명회 대기 신청", description = "(해당 회차에 대한 신청 인원이 꽉찬 경우) 설명회 대기를 신청합니다.")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long registerWaiting(@Login Long userId, @RequestBody @Validated WaitingRegisterDto waitingRegister){
        Long ptDateId = waitingRegister.getPtDateId();
        return waitingService.watingParticipation(userId, ptDateId).getId();
    }

    /**
     * 작성자: 현승구
     * 작성내용: 설명회 대기 신청 취소
     */
    @Operation(summary = "설명회 대기 취소", description = "설명회 대기 신청을 철회합니다")
    @DeleteMapping("{waitingId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long cancelWaiting(@Login Long userId, @PathVariable("waitingId") Long waitingId) {
        return waitingService.cancelParticipation(waitingId, userId);
    }

}