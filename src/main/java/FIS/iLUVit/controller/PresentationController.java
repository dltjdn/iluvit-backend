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
     * 작성자: 현승구
     * 작성내용: 필터 기반 설명회 검색
     */
    @PostMapping("search")
    public SliceImpl<PresentationForUserResponse> getPresentationByFilter(@RequestBody PresentationSearchFilterDto dto, Pageable pageable){
        return presentationService.findByFilter(dto.getAreas(), dto.getTheme(), dto.getInterestedAge(), dto.getKindOf(), dto.getSearchContent(), pageable);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 설명회 전체 조회
     * 비고: 현재날짜에 맞춰서 설명회 기간에 있으면 반환 그렇지 않으면 반환 하지않음
     */
    @GetMapping("info/center/{centerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PresentationDetailResponse> getAllPresentation(@PathVariable("centerId") Long centerId, @Login Long userId){
        return presentationService.findPresentationByCenterIdAndDate(centerId, userId);
    }


    /**
     * TEACHER
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 설명회 정보 저장
     * 비고: 설명회 회차 정보 저장 포함
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public PresentationResponse createPresentationInfo(@RequestBody @Validated PresentationDetailRequest request,
                                                         @Login Long userId){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        return new PresentationResponse(presentationService.saveInfoWithPtDate(request, userId));
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 설명회 이미지 저장
     */
    @PatchMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PresentationResponse updatePresentationInfo(@RequestBody @Validated PresentationRequest request,
                                                       @Login Long userId){
        return new PresentationResponse(presentationService.modifyInfoWithPtDate(request, userId));
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 설명회 정보 수정
     * 비고: 설명회 회차 정보 수정 포함
     */
    @Transactional
    @PostMapping("{presentationId}/image")
    @ResponseStatus(HttpStatus.CREATED)
    public PresentationResponse createPresentationImage(@PathVariable("presentationId") Long presentationId,
                                                          @RequestPart(required = false) List<MultipartFile> images,
                                                          @Login Long userId) {
        if (userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return new PresentationResponse(presentationService.saveImageWithPtDate(presentationId, images, userId));
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 설명회 이미지 수정
     */
    @Transactional
    @PatchMapping("{presentationId}/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PresentationResponse updatePresentationImage(@PathVariable("presentationId") Long presentationId,
                                                        @RequestPart(required = false) List<MultipartFile> images,
                                                        @Login Long userId){
        return new PresentationResponse(presentationService.modifyImageWithPtDate(presentationId, images, userId));
    }

    /**
     * 작성자: 현승구
     * 작성내용: 교사용 설명회 전체 조회
     */
    @GetMapping("center/{centerId}")
    public List<PresentationForTeacherResponse> getAllPresentationForTeacher(@Login Long userId,
                                                                             @PathVariable("centerId") Long centerId,
                                                                             Pageable pageable){
        return presentationService.findPresentationListByCenterId(userId, centerId, pageable);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 설명회 상세 조회
     */
    @GetMapping("{presentationId}")
    public PresentationDetailResponse getPresentationDetails(@PathVariable("presentationId") Long presentationId){
        return presentationService.findPresentationDetail(presentationId);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 설명회 예약 학부모 전체 조회(예약명단)
     */
    @GetMapping("pt-date/{ptDateId}/participating")
    public List<ParentDto> getParentByParticipate(@Login Long userId, @PathVariable("ptDateId") Long ptDateId){
        return presentationService.findPtDateParticipatingParents(userId, ptDateId);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 설명회 대기 학부모 전체 조회(대기명단)
     */
    @GetMapping("pt-date/{ptDateId}/waiting")
    public List<ParentDto> getParentWait(@Login Long userId, @PathVariable("ptDateId") Long ptDateId){
        return presentationService.findPtDateWaitingParents(userId, ptDateId);
    }

}
