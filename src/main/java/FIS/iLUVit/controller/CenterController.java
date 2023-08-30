package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.center.*;
import FIS.iLUVit.dto.center.CenterAndDistancePreviewDto;
import FIS.iLUVit.dto.center.CenterMapPreviewDto;
import FIS.iLUVit.service.CenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "시설 API")
@RequestMapping("center")
public class CenterController {

    private final CenterService centerService;

    /**
     * COMMON
     */

    /**
     * 작성자: 현승구
     * 작성내용: 시설 전체 조회
     */
    @Operation(summary = "주변 시설 전체 조회", description = "주변 시설 전체를 조회합니다.")
    @PostMapping("search/all")
    public List<CenterMapPreviewDto> getAllCenter(@RequestBody @Validated CenterSearchMapDto dto){
        return centerService.findCenterByFilterForMap(dto.getLongitude(), dto.getLatitude(), dto.getDistance(), dto.getSearchContent());
    }

    /**
     * 수정사항 - 베너에서 테마 보여주기 + 리스트(맵)에서 bookmark 내용 추가 + 검색 api 개발
     */

    /**
     * 작성자: 현승구
     * 작성내용: 필터 기반 시설 조회
     * 비고: center 검색 정보 반환 front 검색인자 값 - 위도 경도 지도와 관련하여 api 던져준다
     */
    @Operation(summary = "필터 기반 주변 시설 조회", description = "필터 기반으로 주변 시설 전체를 조회합니다.")
    @PostMapping("search")
    public SliceImpl<CenterAndDistancePreviewDto> getCenterByFilter(@RequestBody @Validated CenterSearchMapFilterDto dto,
                                                                           @Login Long userId,
                                                                           Pageable pageable){
        return centerService.findCenterByFilterForMapList(dto.getLongitude(), dto.getLatitude(), dto.getCenterIds(), userId, dto.getKindOf(), pageable);
    }

    /**
     * 작성자: 현승구
     * 작성내용  시설 상세 조회
     * 비고 : Id 기반 center 정보값 반환하기 기본정보 + 프로그램 + 기본시설 + 부가시설 반환 <p>
     * 개발 추가사항: 사진, 영상 정보 반환할 것 추가하기
     */
    @Operation(summary = "시설 상세 조회", description = "시설 상세 정보를 조회합니다.")
    @GetMapping("{centerId}/info")
    public CenterResponse getCenterDetails(@PathVariable("centerId") Long id){
        return centerService.findCenterDetailsByCenter(id);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 시설 상세 조회(미리보기 배너)
     * 비고: id 기반 으로 센터 클릭시 배너로 나올 center 이름, 모집 상황 반환할 api
     */
    @Operation(summary = "시설 상세 조회 (미리보기 배너)", description = "시설 상세 정보의 미리보기를 조회합니다.")
    @GetMapping("{centerId}/recruit")
    public CenterBannerResponse getCenterDetailsForBanner(@PathVariable("centerId") Long centerId, @Login Long userId){
        return centerService.findCenterBannerByCenter(centerId, userId);
    }


    /**
     * PARENT
     */

    /**
     * 작성자: 현승구
     * 작성내용: 추천 시설 전체 조회
     * 비고: 메인 화면에서 띄워 줄 센터 Banner에 대한 내용 기본적으로 Login이 되어 있어야하는 상태이며 관심 테마 설정이 되어있어야한다
     */
    @Operation(summary = "추천 시설 전체 조회", description = "학부모의 관심 키워드에 따른 추천 시설을 조회합니다.")
    @GetMapping("theme")
    public List<CenterRecommendDto> getAllCenterByTheme(@Login Long userId){
        return centerService.findRecommendCenterWithTheme(userId);
    }


    /**
     * TEACHER
     */

    /**
     * 작성자: 이창윤
     * 작성내용: 시설 정보 수정
     */
    @Operation(summary = "시설 정보 수정", description = "교사가 시설의 정보를 수정합니다")
    @PatchMapping("{centerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long updateCenterInfo(@PathVariable("centerId") Long centerId,
                                 @Login Long userId,
                                 @RequestBody @Validated CenterDetailRequest requestDto){

        return centerService.modifyCenterInfo(centerId, userId, requestDto);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 시설 이미지 수정
     */
    @Operation(summary = "시설 이미지 수정", description = "교사가 시설의 이미지를 수정합니다.")
    @PatchMapping("{centerId}/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Long updateCenterImage(@PathVariable("centerId") Long centerId,
                             @Login Long userId,
                             @RequestPart(required = false) List<MultipartFile> infoImages,
                             @RequestPart(required = false) MultipartFile profileImage){
        return centerService.modifyCenterImage(centerId, userId, infoImages, profileImage);
    }

}
