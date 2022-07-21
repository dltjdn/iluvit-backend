package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.WaitingCancelDto;
import FIS.iLUVit.controller.dto.WaitingRegisterDto;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/waiting")
    public Long register(@Login Long userId, @RequestBody WaitingRegisterDto dto){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_AUTHORIZED_USER);
        Long ptDateId = dto.getPtDateId();
        return waitingService.register(userId, ptDateId).getId();
    }

    @DeleteMapping("/waiting")
    public Long cancel(@Login Long userId, @RequestBody WaitingCancelDto dto){
        Long waitingId = dto.getWaitingId();
        return waitingService.cancel(waitingId, userId);
    }

}
