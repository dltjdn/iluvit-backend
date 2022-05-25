package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.MyParticipationsDto;
import FIS.iLUVit.controller.dto.ParticipationCancelRequestDto;
import FIS.iLUVit.controller.dto.ParticipationRegisterRequestDto;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * 설명회 신청
     * @return
     */
    @PostMapping("/participation")
    public Long register(@Login Long userId, @RequestBody ParticipationRegisterRequestDto dto){
        return participationService.register(userId, dto.getPtDateId());
    }

    /**
     * 설명회 취소
     */
    @PatchMapping("/participation")
    public Long cancel(@Login Long userId, @RequestBody ParticipationCancelRequestDto dto){
        return participationService.cancel(userId, dto.getParticipationId());
    }

    @GetMapping("/participation/parent")
    public Map<Status, List<MyParticipationsDto>> getMyParticipation(@Login Long userId){
        return participationService.getMyParticipation(userId);
    }
}