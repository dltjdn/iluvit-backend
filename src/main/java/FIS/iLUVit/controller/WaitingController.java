package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.waiting.WaitingCancelDto;
import FIS.iLUVit.dto.waiting.WaitingRegisterDto;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.service.WaitingService;
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
@RequestMapping("waiting")
public class WaitingController {

    private final WaitingService waitingService;

    /**
     * PARENT
     */

    /**
     * 설명회 대기 신청
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long register(@Login Long userId, @RequestBody @Validated WaitingRegisterDto dto){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        Long ptDateId = dto.getPtDateId();
        return waitingService.register(userId, ptDateId).getId();
    }

    /**
     * 설명회 대기 신청 취소
     */
    @DeleteMapping("{waitingId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long cancel(@Login Long userId, @PathVariable("waitingId") Long waitingId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return waitingService.cancel(waitingId, userId);
    }

}
