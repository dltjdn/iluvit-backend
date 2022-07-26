package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterMapPreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import FIS.iLUVit.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CenterController {

    private final CenterService centerService;

    /**
     * 수정사항 - 베너에서 테마 보여주기 + 리스트(맵)에서 bookmark 내용 추가 + 검색 api 개발
     */

    /**
     * center 검색 정보 반환 front 검색인자 값 - 위도 경도 지도와 관련하여 api 던져준다
     */
    @PostMapping("/center/map/list")
    public SliceImpl<CenterAndDistancePreview> searchByFilterForMapList(@RequestBody @Validated CenterSearchMapFilterDTO dto,
                                                                        @Login Long userId,
                                                                        Pageable pageable){
        return centerService.findByFilterForMapList(dto.getLongitude(), dto.getLatitude(), dto.getCenterIds(), userId, dto.getKindOf(), pageable);
    }

    @GetMapping("/center/map/search/all")
    public List<CenterMapPreview> searchByFilterForMap(@ModelAttribute @Validated CenterSearchMapDto dto){
        return centerService.findByFilterForMap(dto.getLongitude(), dto.getLatitude(), dto.getDistance(), dto.getSearchContent());
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
    public CenterBannerResponseDto centerBanner(@PathVariable("center_id") Long id, @Login Long userId){
        return centerService.findBannerById(id, userId);
    }

    /**
     * 메인 화면에서 띄워 줄 센터 Banner에 대한 내용 기본적으로 Login이 되어 있어야하는 상태이며 관심 테마 설정이 되어있어야한다.
     * 회원로직 완료후에 작업 시작
     */
    @GetMapping("/center/theme")
    public List<CenterRecommendDto> centerThemeBanner(@Login Long userId){
        return centerService.findCenterForParent(userId);
    }

    /**
     * 시설 정보 수정
     */
    @PatchMapping("/center/{centerId}")
    public Long modifyCenter(@PathVariable("centerId") Long centerId,
                             @Login Long userId,
                             @RequestPart @Validated CenterModifyRequestDto requestDto,
                             @RequestPart(required = false) List<MultipartFile> infoImages,
                             @RequestPart(required = false) MultipartFile profileImage){
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        return centerService.modifyCenter(centerId, userId, requestDto, infoImages, profileImage);
    }

    /**
    *   작성날짜: 2022/06/20 3:49 PM
    *   작성자: 이승범
    *   작성내용: 회원가입, 이직 과정에서 center 정보 가져오기
    */
    @GetMapping("/center/signup")
    public Slice<CenterInfoDto> centerInfoForSignup(@ModelAttribute CenterInfoRequest request, Pageable pageable) {
        return centerService.findCenterForSignup(request, pageable);
    }

    /**
    *   작성날짜: 2022/06/24 10:29 AM
    *   작성자: 이승범
    *   작성내용: 아이추가 과정에서 필요한 센터정보 가져오기
    */
    @GetMapping("/center/child/add")
    public Slice<CenterInfoDto> centerInfoForAddChild(@ModelAttribute CenterInfoRequest request, Pageable pageable) {
        return centerService.findCenterForAddChild(request, pageable);
    }

    /**
     *   작성날짜: 2022/07/04 2:26 PM
     *   작성자: 이승범
     *   작성내용: 찜한 시설 리스트
     */
    @GetMapping("/parent/prefer")
    public Slice<CenterPreview> findCentersByPrefer(@Login Long userId, Pageable pageable) {
        return centerService.findCentersByPrefer(userId, pageable);
    }

}
