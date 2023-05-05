package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.parent.ParticipationListDto;
import FIS.iLUVit.dto.presentation.PtDateRequest;
import FIS.iLUVit.domain.iluvit.enumtype.Status;
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
     * 작성자: 현승구
     * 작성내용: 설명회 신청
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long registerParticipation(@Login Long userId, @RequestBody @Validated PtDateRequest dto){
        return participationService.register(userId, dto.getPtDateId());
    }

    /**
     * 작성자: 현승구
     * 작성내용: 설명회 취소
     * 비고: 대가자 있을 경우 자동 합류
     */
    @PatchMapping("{participationId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long cancelParticipation(@Login Long userId, @PathVariable("participationId") Long participationId){
        return participationService.cancel(userId, participationId);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 신청한/취소한 설명회 전체 조회
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<Status, List<ParticipationListDto>> getAllParticipation(@Login Long userId){
        return participationService.getMyParticipation(userId);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 신청한 설명회 전체 조회
     */
    @GetMapping("join")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<ParticipationListDto> getRegisterParticipation(@Login Long userId, Pageable pageable){
        return participationService.getMyJoinParticipation(userId, pageable);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 신청을 취소한 설명회 전체 조회
     */
    @GetMapping("cancel")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<ParticipationListDto> getCancelParticipation(@Login Long userId, Pageable pageable){
        return participationService.getMyCancelParticipation(userId, pageable);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 대기를 신청한 설명회 전체 조회
     */
    @GetMapping("waiting")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Slice<ParticipationListDto> getWaitingParticipation(@Login Long userId, Pageable pageable){
        return participationService.getMyWaiting(userId, pageable);
    }

}
