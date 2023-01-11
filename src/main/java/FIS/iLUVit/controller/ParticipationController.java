package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.MyParticipationsDto;
import FIS.iLUVit.controller.dto.ParticipationCancelRequestDto;
import FIS.iLUVit.controller.dto.ParticipationRegisterRequestDto;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/participation")
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * 설명회 신청
     *
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long register(@Login Long userId, @RequestBody @Validated ParticipationRegisterRequestDto dto){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.register(userId, dto.getPtDateId());
    }

    /**
     * 설명회 취소
     */
    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long cancel(@Login Long userId, @RequestBody @Validated ParticipationCancelRequestDto dto){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.cancel(userId, dto.getParticipationId());
    }

    @GetMapping("/parent")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<Status, List<MyParticipationsDto>> getMyParticipation(@Login Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.getMyParticipation(userId);
    }

    @GetMapping("/join/parent")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<MyParticipationsDto> getMyJoinParticipation(@Login Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.getMyJoinParticipation(userId, pageable);
    }

    @GetMapping("/cancel/parent")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<MyParticipationsDto> getMyCancelParticipation(@Login Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.getMyCancelParticipation(userId, pageable);
    }

    @GetMapping("/waiting/parent")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<MyParticipationsDto> getMyWaiting(@Login Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.getMyWaiting(userId, pageable);
    }

}
