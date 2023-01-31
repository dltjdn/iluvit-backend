package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.*;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.dto.center.CenterAndDistancePreviewDto;
import FIS.iLUVit.dto.center.CenterMapPreviewDto;
import FIS.iLUVit.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("center")
public class CenterController {

    private final CenterService centerService;

    /**
     * COMMON
     */

    /**
     * 주변 시설 검색
     */
    @GetMapping("search/all")
    public List<CenterMapPreviewDto> searchByFilterForMap(@ModelAttribute @Validated CenterSearchMapDto dto){
        return centerService.findByFilterForMap(dto.getLongitude(), dto.getLatitude(), dto.getDistance(), dto.getSearchContent());
    }

    /**
     * 수정사항 - 베너에서 테마 보여주기 + 리스트(맵)에서 bookmark 내용 추가 + 검색 api 개발
     */

    /**
     * 필터 기반 시설 검색
     * center 검색 정보 반환 front 검색인자 값 - 위도 경도 지도와 관련하여 api 던져준다
     */
    @PostMapping("search")
    public SliceImpl<CenterAndDistancePreviewDto> searchByFilterForMapList(@RequestBody @Validated CenterSearchMapFilterDto dto,
                                                                           @Login Long userId,
                                                                           Pageable pageable){
        return centerService.findByFilterForMapList(dto.getLongitude(), dto.getLatitude(), dto.getCenterIds(), userId, dto.getKindOf(), pageable);
    }

    /**
     * 모달창의 시설 상세 정보 조회
     * Id 기반 center 정보값 반환하기 기본정보 + 프로그램 + 기본시설 + 부가시설 반환 <p>
     * 개발 추가 사항 - 사진, 영상 정보 반환할 것 추가하기
     */
    @GetMapping("{centerId}/info")
    public CenterResponse centerInfo(@PathVariable("centerId") Long id){
        return centerService.findInfoById(id);
    }

    /**
     * 시설 클릭 시 나올 모달창의 배너 정보 조회
     * id 기반 으로 센터 클릭시 배너로 나올 center 이름, 모집 상황 반환할 api
     */
    @GetMapping("{centerId}/recruit")
    public CenterBannerResponse centerBanner(@PathVariable("centerId") Long id, @Login Long userId){
        return centerService.findBannerById(id, userId);
    }

    /**
     * 메인 페이지용 시설 배너 정보 조회
     * 메인 화면에서 띄워 줄 센터 Banner에 대한 내용 기본적으로 Login이 되어 있어야하는 상태이며 관심 테마 설정이 되어있어야한다.
     * 회원로직 완료후에 작업 시작
     */
    @GetMapping("theme")
    public List<CenterRecommendDto> centerThemeBanner(@Login Long userId){
        return centerService.findCenterForParent(userId);
    }


    /**
     * TEACHER
     */

    /**
     * 작성자: 이창윤
     * 리액트 네이티브용 시설 정보 수정
     */
    @PatchMapping("{centerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long modifyCenterInfo(@PathVariable("centerId") Long centerId,
                                 @Login Long userId,
                                 @RequestBody @Validated CenterDetailRequest requestDto){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        log.info("requestDto = {}", requestDto);
        return centerService.modifyCenterInfo(centerId, userId, requestDto);
    }

    /**
     * 작성자: 이창윤
     * 리액트 네이티브용 시설 정보 이미지 수정
     */
    @PatchMapping("{centerId}/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long modifyCenterImage(@PathVariable("centerId") Long centerId,
                             @Login Long userId,
                             @RequestPart(required = false) List<MultipartFile> infoImages,
                             @RequestPart(required = false) MultipartFile profileImage){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return centerService.modifyCenterImage(centerId, userId, infoImages, profileImage);
    }

}
