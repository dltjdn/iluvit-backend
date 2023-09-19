package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.presentation.*;
import FIS.iLUVit.service.PresentationService;
import FIS.iLUVit.dto.parent.ParentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("presentation")
public class PresentationController {

    private final PresentationService presentationService;


    /**
     * COMMON
     */

    /**
     * 필터 기반 설명회 검색
     */
    @PostMapping("search")
    public ResponseEntity<Slice<PresentationSearchFilterResponse>> getPresentationByFilter(@RequestBody PresentationSearchFilterRequest request, Pageable pageable){
        Slice<PresentationSearchFilterResponse> responses = presentationService.findPresentationByFilter(request, pageable);
        return ResponseEntity.ok().body(responses);
    }

    /**
     * (현재 진행 중인) 설명회 전체 조회
     */
    @GetMapping("info/center/{centerId}")
    public ResponseEntity<List<PresentationFindOneResponse>> getAllPresentation(@Login Long userId, @PathVariable("centerId") Long centerId){
        List<PresentationFindOneResponse> responses = presentationService.findAllPresentation( userId, centerId);
        return ResponseEntity.ok().body(responses);
    }


    /**
     * TEACHER
     */

    /**
     * 설명회 정보 저장 (설명회 회차 정보 저장 포함)
     */
    @PostMapping("")
    public ResponseEntity<PresentationCreateResponse> createPresentationInfo(@Login Long userId, @RequestBody @Validated PresentationCreateRequest request){
        PresentationCreateResponse response = presentationService.createPresentationInfoWithPtDate(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * 설명회 정보 수정 ( 설명회 회차 정보 수정 포함)
     */
    @PatchMapping("")
    public ResponseEntity<Void> updatePresentationInfo( @Login Long userId, @RequestBody @Validated PresentationUpdateRequest request){
        presentationService.updatePresentationInfoWithPtDate(userId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 설명회 이미지 저장
     */
    @Transactional
    @PostMapping("{presentationId}/image")
    public ResponseEntity<Void> createPresentationImage( @PathVariable("presentationId") Long presentationId,
                                                          @RequestPart(required = false) List<MultipartFile> images) {
        presentationService.savePresentationImageWithPtDate(presentationId, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 설명회 이미지 수정
     */
    @Transactional
    @PatchMapping("{presentationId}/image")
    public ResponseEntity<Void> updatePresentationImage(@Login Long userId, @PathVariable("presentationId") Long presentationId,
                                                          @RequestPart(required = false) List<MultipartFile> images){
        presentationService.modifyPresentationImageWithPtDate(userId, presentationId, images);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 교사용 설명회 전체 조회
     */
    @GetMapping("center/{centerId}")
    public List<PresentationForTeacherResponse> getAllPresentationForTeacher(@Login Long userId, @PathVariable("centerId") Long centerId, Pageable pageable){
        return presentationService.findPresentationListByCenter(userId, centerId, pageable);
    }

    /**
     * 설명회 상세 조회
     */
    @GetMapping("{presentationId}")
    public ResponseEntity<PresentationFindOneResponse> getPresentationDetails(@PathVariable("presentationId") Long presentationId){
        PresentationFindOneResponse response = presentationService.findPresentationDetails(presentationId);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 설명회 예약 학부모 전체 조회 (예약명단)
     */
    @GetMapping("pt-date/{ptDateId}/participating")
    public ResponseEntity<List<ParentResponse>> getParentParticipate(@Login Long userId, @PathVariable("ptDateId") Long ptDateId){
        List<ParentResponse> responses = presentationService.findParentListWithRegisterParticipation(userId, ptDateId);
        return ResponseEntity.ok().body(responses);
    }

    /**
     * 설명회 대기 학부모 전체 조회 (대기명단)
     */
    @GetMapping("pt-date/{ptDateId}/waiting")
    public ResponseEntity<List<ParentResponse>> getParentWait(@Login Long userId, @PathVariable("ptDateId") Long ptDateId){
        List<ParentResponse> responses = presentationService.findParentListWithWaitingParticipation(userId, ptDateId);
        return ResponseEntity.ok().body(responses);
    }

}
