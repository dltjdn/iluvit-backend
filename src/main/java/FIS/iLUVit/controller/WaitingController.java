package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.WaitingRegisterDto;
import FIS.iLUVit.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/waiting")
    public Long register(@Login Long userId, @RequestBody WaitingRegisterDto dto){
        Long ptDateId = dto.getPtDateId();
        return waitingService.register(userId, ptDateId);
    }
}
