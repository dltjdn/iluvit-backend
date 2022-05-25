package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.WaitingCancelDto;
import FIS.iLUVit.controller.dto.WaitingRegisterDto;
import FIS.iLUVit.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/waiting")
    public Long register(@Login Long userId, @RequestBody WaitingRegisterDto dto){
        Long ptDateId = dto.getPtDateId();
        return waitingService.register(userId, ptDateId);
    }

    @DeleteMapping("/waiting")
    public Long cancel(@Login Long userid, @RequestBody WaitingCancelDto dto){
        Long waitingId = dto.getWaitingId();
        return waitingService.cancel(waitingId);
    }
}
