package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.presentation.*;
import FIS.iLUVit.dto.presentation.*;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.service.PresentationService;
import FIS.iLUVit.service.UserService;
import FIS.iLUVit.dto.parent.ParentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
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
     * 필터 기반으로 presentation 검색
     */
    @PostMapping("search")
    public SliceImpl<PresentationForUserResponse> searchByFilterAndMap(@RequestBody PresentationSearchFilterDto dto, Pageable pageable){
        return presentationService.findByFilter(dto.getAreas(), dto.getTheme(), dto.getInterestedAge(), dto.getKindOf(), dto.getSearchContent(), pageable);
    }

    /**
     * 모달창으로 나오는 시설 정보 + 설명회 + 리뷰 정보가 나오는 곳에서 보여줄 설명회에 대한 내용 <p>
     * 현재날짜에 맞춰서 설명회 기간에 있으면 반환 그렇지 않으면 반환 하지않음 <p>
     * 내용 - 신청기간, 내용, 사진, 동영상, 신청할 수 있는 설명회 목록?
     */
    @GetMapping("info/center/{centerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PresentationDetailResponse> findPresentationByCenterId(@PathVariable("centerId") Long centerId, @Login Long userId){
        return presentationService.findPresentationByCenterIdAndDate(centerId, userId);
    }


    /**
     * TEACHER
     */

    /**
     * 원장/ 선생의 presentation 등록 PtDate 설정하기
     * 설명회 정보 저장
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public PresentationResponse registerPresentationInfo(@RequestBody @Validated PresentationDetailRequest request,
                                                         @Login Long userId){

        return new PresentationResponse(presentationService.saveInfoWithPtDate(request, userId));
    }

    /**
     * 설명회 정보 수정
     */
    @PatchMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PresentationResponse modifyPresentationInfo(@RequestBody @Validated PresentationRequest request,
                                                       @Login Long userId){
        return new PresentationResponse(presentationService.modifyInfoWithPtDate(request, userId));
    }

    /**
     * 원장/ 선생의 presentation 등록 PtDate 설정하기
     * 설명회 이미지 저장
     */
    @Transactional
    @PostMapping("{presentationId}/image")
    @ResponseStatus(HttpStatus.CREATED)
    public PresentationResponse registerPresentationImage(@PathVariable("presentationId") Long presentationId,
                                                          @RequestPart(required = false) List<MultipartFile> images,
                                                          @Login Long userId) {
        return new PresentationResponse(presentationService.saveImageWithPtDate(presentationId, images, userId));
    }

    /**
     * 설명회 이미지 수정
     */
    @Transactional
    @PatchMapping("{presentationId}/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PresentationResponse modifyPresentationImage(@PathVariable("presentationId") Long presentationId,
                                                        @RequestPart(required = false) List<MultipartFile> images,
                                                        @Login Long userId){
        return new PresentationResponse(presentationService.modifyImageWithPtDate(presentationId, images, userId));
    }

    /**
     * 설명회 목록 조회
     */
    @GetMapping("center/{centerId}")
    public List<PresentationForTeacherResponse> findMyCenterPresentationList(@Login Long userId,
                                                                             @PathVariable("centerId") Long centerId,
                                                                             Pageable pageable){
        return presentationService.findPresentationListByCenterId(userId, centerId, pageable);
    }

    /**
     * 설명회 자세히 보기
     */
    @GetMapping("{presentationId}")
    public PresentationDetailResponse findMyCenterPresentation(@PathVariable("presentationId") Long presentationId){
        return presentationService.findPresentationDetail(presentationId);
    }

    /**
     * 설명회를 신청한 사람들의 목록 반환 이름, 전화번호
     */
    @GetMapping("pt-date/{ptDateId}/participating")
    public List<ParentDto> findParentParticipate(@Login Long userId, @PathVariable("ptDateId") Long ptDateId){
        return presentationService.findPtDateParticipatingParents(userId, ptDateId);
    }

    /**
     * 대기를 신청한 사람들의 목록 반환 이름, 전화번호
     */
    @GetMapping("pt-date/{ptDateId}/waiting")
    public List<ParentDto> findParentWait(@Login Long userId, @PathVariable("ptDateId") Long ptDateId){
        return presentationService.findPtDateWaitingParents(userId, ptDateId);
    }

}
