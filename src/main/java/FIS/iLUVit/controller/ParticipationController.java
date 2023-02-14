package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.parent.ParticipationListDto;
import FIS.iLUVit.dto.presentation.PtDateRequest;
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
@RequestMapping("participation")
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * PARENT
     */

    /**
     * 설명회 신청
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long register(@Login Long userId, @RequestBody @Validated PtDateRequest dto){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.register(userId, dto.getPtDateId());
    }

    /**
     * 설명회 취소
     * 대가자 있을 경우 자동 합류
     */
    @PatchMapping("{participationId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long cancel(@Login Long userId, @PathVariable("participationId") Long participationId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.cancel(userId, participationId);
    }

    /**
     * 내 설명회 신청 내역 조회
     * 내가 신청한 혹은 취소한 설명회 내역
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<Status, List<ParticipationListDto>> getMyParticipation(@Login Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.getMyParticipation(userId);
    }

    /**
     * 내가 신청한 설명회 목록 조회
     */
    @GetMapping("join")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<ParticipationListDto> getMyJoinParticipation(@Login Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.getMyJoinParticipation(userId, pageable);
    }

    /**
     * 내가 신청을 취소한 설명회 목록 조회
     */
    @GetMapping("cancel")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<ParticipationListDto> getMyCancelParticipation(@Login Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.getMyCancelParticipation(userId, pageable);
    }

    /**
     * 내가 대기를 신청한 설명회 목록 조회
     */
    @GetMapping("waiting")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<ParticipationListDto> getMyWaiting(@Login Long userId, Pageable pageable){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return participationService.getMyWaiting(userId, pageable);
    }

}
