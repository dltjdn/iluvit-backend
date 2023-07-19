package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.participation.ParticipationDto;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.dto.participation.ParticipationWithStatusDto;
import FIS.iLUVit.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("{ptDateId}")
    public ResponseEntity<Void> registerParticipation(@Login Long userId, @PathVariable("ptDateId") Long ptDateId){
        participationService.registerParticipation(userId, ptDateId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 설명회 취소 ( 대가자 있을 경우 자동 합류 )
     */
    @PatchMapping("{participationId}")
    public ResponseEntity<Void> cancelParticipation(@Login Long userId, @PathVariable("participationId") Long participationId){
        participationService.cancelParticipation(userId, participationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 신청한/신청 취소한 설명회 전체 조회
     */
    @GetMapping("")
    public ResponseEntity<List<ParticipationWithStatusDto>> getAllParticipation(@Login Long userId){
        List<ParticipationWithStatusDto> participationWithStatusDtos = participationService.findAllParticipationByUser(userId);
        return ResponseEntity.ok(participationWithStatusDtos);
    }

    /**
     * 신청한 설명회 전체 조회
     */
    @GetMapping("join")
    public ResponseEntity<Slice<ParticipationDto>> getRegisterParticipation(@Login Long userId, Pageable pageable){
        Slice<ParticipationDto> participationDtos = participationService.findRegisterParticipationByUser(userId, pageable);
        return ResponseEntity.ok(participationDtos);
    }

    /**
     * 신청 취소한 설명회 전체 조회
     */
    @GetMapping("cancel")
    public ResponseEntity<Slice<ParticipationDto>> getCancelParticipation(@Login Long userId, Pageable pageable){
        Slice<ParticipationDto> participationDtos = participationService.findCancelParticipationByUser(userId, pageable);
        return ResponseEntity.ok(participationDtos);
    }

    /**
     * 대기를 신청한 설명회 전체 조회
     */
    @GetMapping("waiting")
    public ResponseEntity<Slice<ParticipationDto>> getWaitingParticipation(@Login Long userId, Pageable pageable){
        Slice<ParticipationDto> participationDtos = participationService.findWaitingParticipationByUser(userId, pageable);
        return ResponseEntity.ok(participationDtos);
    }
}
