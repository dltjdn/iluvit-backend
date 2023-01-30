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

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long register(@Login Long userId, @RequestBody @Validated WaitingRegisterDto dto){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        Long ptDateId = dto.getPtDateId();
        return waitingService.register(userId, ptDateId).getId();
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long cancel(@Login Long userId, @RequestBody @Validated WaitingCancelDto dto){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        Long waitingId = dto.getWaitingId();
        return waitingService.cancel(waitingId, userId);
    }

}
