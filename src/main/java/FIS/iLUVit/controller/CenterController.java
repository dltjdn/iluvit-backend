package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.dto.center.*;
import FIS.iLUVit.dto.center.CenterAndDistancePreviewDto;
import FIS.iLUVit.dto.center.CenterMapPreviewDto;
import FIS.iLUVit.service.CenterService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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
     * 시설 전체 조회
     */
    @PostMapping("search/all")
    public ResponseEntity<List<CenterMapPreviewDto>> getAllCenter(@RequestParam("searchContent") String searchContent, @RequestBody @Validated CenterSearchMapDto centerSearchMapDto){
        List<CenterMapPreviewDto> centerByFilterForMap = centerService.findCenterByFilterForMap(searchContent, centerSearchMapDto);
        return ResponseEntity.ok(centerByFilterForMap);
    }


    /**
     * 유저가 설정한 필터 기반 시설 조회
     */
    @PostMapping("search")
    public ResponseEntity<SliceImpl<CenterAndDistancePreviewDto>> getCenterByFilter(@Login Long userId, @RequestParam("kindOf") KindOf kindOf, @RequestBody @Validated CenterSearchMapFilterDto centerSearchMapFilterDto, Pageable pageable){
        SliceImpl<CenterAndDistancePreviewDto> centerByFilterForMapList = centerService.findCenterByFilterForMapList(userId,kindOf, centerSearchMapFilterDto, pageable);
        return ResponseEntity.ok(centerByFilterForMapList);
    }

    /**
     * 시설 상세 조회
     */
    @GetMapping("{centerId}/info")
    public ResponseEntity<CenterResponse> getCenterDetails(@PathVariable("centerId") Long centerId){
        CenterResponse centerDetailsByCenter = centerService.findCenterDetailsByCenter(centerId);
        return ResponseEntity.ok(centerDetailsByCenter);
    }

    /**
     * 미리보기 배너 용 시설 상세 조회
     */
    @GetMapping("{centerId}/recruit")
    public ResponseEntity<CenterBannerResponse> getCenterDetailsForBanner(@Login Long userId, @PathVariable("centerId") Long centerId){
        CenterBannerResponse centerBannerByCenter = centerService.findCenterBannerByCenter(userId, centerId);
        return ResponseEntity.ok(centerBannerByCenter);
    }


    /**
     * PARENT
     */

    /**
     *  추천 시설 전체 조회 ( 학부모가 선택한 관심 테마를 가지고 있는 시설 조회 )
     */
    @GetMapping("theme")
    public ResponseEntity<List<CenterRecommendDto>> getAllCenterByTheme(@Login Long userId){
        List<CenterRecommendDto> recommendCenterWithTheme = centerService.findRecommendCenterWithTheme(userId);
        return ResponseEntity.ok(recommendCenterWithTheme);
    }


    /**
     * TEACHER
     */

    /**
     * 시설 정보 수정
     */
    @PatchMapping("{centerId}")
    public ResponseEntity<Void> updateCenterInfo(@Login Long userId, @PathVariable("centerId") Long centerId,
                                                 @RequestBody @Validated CenterDetailRequest centerDetailRequest){

        centerService.modifyCenterInfo(userId, centerId, centerDetailRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 시설 이미지 수정
     */
    @PatchMapping("{centerId}/image")
    public ResponseEntity<Void> updateCenterImage(@Login Long userId, @PathVariable("centerId") Long centerId,
                                                  @Valid @ModelAttribute CenterImageRequest centerImageRequest){

        centerService.modifyCenterImage(userId, centerId, centerImageRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
