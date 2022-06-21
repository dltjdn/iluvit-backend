package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import FIS.iLUVit.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CenterController {

    private final CenterService centerService;

    /**
     * 시설 둘러보기 페이지
     */
    @GetMapping("/center/preview")
    public List<CenterPreview> searchPreview(@ModelAttribute Area area){
        Slice<CenterPreview> centerPreviews = centerService.findByFilter(Collections.singletonList(area),
                null,
                null,
                KindOf.ALL,
                PageRequest.of(0, 5));
        return centerPreviews.getContent();
    }

    /**
     * center 검색 정보 반환 front 검색인자 값 - 시도, 시군구 값(list) 그리고 offset 과 갯수 몇개 가져올건지 <P>
     * 반환값으로 center Preview 정보와 startIndex와 endIndex 보내준다.
     * QueryDsl 에서 또한 Paging 처리를 Pagable을 사용해서 할 수 있으므로 최적화 할 것.
     * @return
     */
    @PostMapping("/center/search")
    public Slice<CenterPreview> searchByFilter(@RequestBody CenterSearchFilterDTO dto, Pageable pageable){
        return centerService.findByFilter(dto.getAreas(), dto.getTheme(), dto.getInterestedAge(), dto.getKindOf(), pageable);
    }

    /**
     * center 검색 정보 반환 front 검색인자 값 - 위도 경도 지도와 관련하여 api 던져준다
     */
    @PostMapping("/center/map/search")
    public List<CenterAndDistancePreview> searchByFilterAndMap(@RequestBody CenterSearchMapFilterDTO dto){
        List<CenterAndDistancePreview> center = centerService.
                findByFilterAndMap(dto.getLongitude(), dto.getLatitude() ,dto.getTheme(), dto.getInterestedAge(), dto.getKindOf(), dto.getDistance());
        return center;
    }

    /**
     * Id 기반 center 정보값 반환하기 기본정보 + 프로그램 + 기본시설 + 부가시설 반환 <p>
     * 개발 추가 사항 - 사진, 영상 정보 반환할 것 추가하기
     */
    @GetMapping("/center/{center_id}/Info")
    public CenterInfoResponseDto centerInfo(@PathVariable("center_id") Long id){
        return centerService.findInfoById(id);
    }

    /**
     * id 기반 으로 센터 클릭시 배너로 나올 center 이름, 모집 상황 반환할 api
     */
    @GetMapping("/center/{center_id}/recruit")
    public CenterBannerResponseDto centerBanner(@PathVariable("center_id") Long id){
        return new CenterBannerResponseDto(centerService.findBannerById(id));
    }

    /**
     * 메인 화면에서 띄워 줄 센터 Banner에 대한 내용 기본적으로 Login이 되어 있어야하는 상태이며 관심 테마 설정이 되어있어야한다.
     * 회원로직 완료후에 작업 시작
     */
    @GetMapping("/center/theme")
    public CenterThemeBannerResponseDto centerThemeBanner(@Login Long userId){
        return new CenterThemeBannerResponseDto(centerService.findCenterForParent(userId));
    }

    /**
     * 시설 정보 수정
     */
    @PatchMapping("/center/{centerId}")
    public Long modifyCenter(@PathVariable("centerId") Long centerId,
                             @Login Long userId,
                             @RequestPart CenterModifyRequestDto requestDto,
                             @RequestPart List<MultipartFile> infoFiles){
        return centerService.modifyCenter(centerId, userId, requestDto, infoFiles);
    }

    /**
    *   작성날짜: 2022/06/20 3:49 PM
    *   작성자: 이승범
    *   작성내용: 회원가입 과정에서 center 정보 가져오기
    */
    @GetMapping("/center/signup")
    public Slice<CenterInfoForSignupDto> centerInfoForSignup(@ModelAttribute CenterInfoForSignupRequest request, Pageable pageable) {
        return centerService.findCenterForSignup(request, pageable);
    }

}
