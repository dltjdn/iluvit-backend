package FIS.iLUVit.domain.participation.controller;

import FIS.iLUVit.domain.participation.domain.Status;
import FIS.iLUVit.domain.participation.dto.ParticipationCreateRequest;
import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.participation.dto.ParticipationResponse;
import FIS.iLUVit.domain.participation.dto.ParticipationWithStatusResponse;
import FIS.iLUVit.domain.participation.service.ParticipationService;
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
    @PostMapping("")
    public ResponseEntity<Long> registerParticipation(@Login Long userId, @RequestBody ParticipationCreateRequest request){
        Long response = participationService.registerParticipation(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 설명회 취소 ( 대가자 있을 경우 자동 합류 )
     */
    @PatchMapping("{participationId}")
    public ResponseEntity<Long> cancelParticipation(@Login Long userId, @PathVariable("participationId") Long participationId){
        Long response = participationService.cancelParticipation(userId, participationId);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 신청한/신청 취소한/대기한 설명회 전체 조회
     */
    @GetMapping("")
    public ResponseEntity<Map<Status, List<ParticipationResponse>>> getAllParticipation(@Login Long userId){
        Map<Status, List<ParticipationResponse>> response = participationService.findAllParticipationByUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 신청한 설명회 전체 조회
     */
    @GetMapping("join")
    public ResponseEntity<Slice<ParticipationResponse>> getRegisterParticipation(@Login Long userId, Pageable pageable){
        Slice<ParticipationResponse> participationDtos = participationService.findRegisterParticipationByUser(userId, pageable);
        return ResponseEntity.ok(participationDtos);
    }

    /**
     * 신청 취소한 설명회 전체 조회
     */
    @GetMapping("cancel")
    public ResponseEntity<Slice<ParticipationResponse>> getCancelParticipation(@Login Long userId, Pageable pageable){
        Slice<ParticipationResponse> participationDtos = participationService.findCancelParticipationByUser(userId, pageable);
        return ResponseEntity.ok(participationDtos);
    }

    /**
     * 대기를 신청한 설명회 전체 조회
     */
    @GetMapping("waiting")
    public ResponseEntity<Slice<ParticipationResponse>> getWaitingParticipation(@Login Long userId, Pageable pageable){
        Slice<ParticipationResponse> participationDtos = participationService.findWaitingParticipationByUser(userId, pageable);
        return ResponseEntity.ok(participationDtos);
    }
}
