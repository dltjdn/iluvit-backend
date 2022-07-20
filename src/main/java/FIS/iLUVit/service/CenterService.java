package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Score;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import FIS.iLUVit.repository.dto.CenterPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CenterService {

    private final CenterRepository centerRepository;
    private final ImageService imageService;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;

    public Slice<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable) {
        return centerRepository.findByFilter(areas, theme, interestedAge, kindOf, pageable);
    }

    public List<CenterAndDistancePreview> findByFilterAndMap(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance) {

        List<CenterAndDistancePreview> results =
                centerRepository.findByMapFilter(longitude, latitude, theme, interestedAge, kindOf, distance);
        results.forEach(result -> result.calculateDistance(longitude, latitude));
        return results;
    }

    public CenterInfoResponseDto findInfoById(Long id) {
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new CenterException("해당 센터 존재하지 않음"));
        // Center 가 id 에 의해 조회 되었으므로 score에 1 추가
        center.addScore(Score.GET);
        CenterInfoResponseDto result = new CenterInfoResponseDto(center);
        result.setProfileImage(imageService.getProfileImage(center));
        result.setInfoImages(imageService.getInfoImages(center));
        return result;
    }

    public CenterBannerResponseDto findBannerById(Long id, Long userId) {
        CenterBannerDto data = userId == null ?
                centerRepository.findBannerById(id) :
                centerRepository.findBannerById(id, userId);

        if(data == null)
            return null;

        List<String> infoImages = imageService.getInfoImages(data.getInfoImages());
        return new CenterBannerResponseDto(data, infoImages);
    }

    public Long modifyCenter(Long centerId, Long userId, CenterModifyRequestDto requestDto, List<MultipartFile> infoImages, MultipartFile profileImage) {
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canWrite(centerId);
        // 해당하는 center 없으면 RuntimeException 반환
        Center center = centerRepository.findById(centerId).orElseThrow(RuntimeException::new);
        imageService.saveInfoImages(infoImages, center);
        imageService.saveProfileImage(profileImage, center);
        center.update(requestDto);
        return center.getId();
    }

    public List<CenterRecommendDto> findCenterForParent(Long userId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException("해당 유저가 존재 하지 않습니다."));
        Theme theme = parent.getTheme();
        return centerRepository.findRecommendCenter(theme, PageRequest.of(0, 10, Sort.by("score")));
    }

    /**
    *   작성날짜: 2022/06/24 10:28 AM
    *   작성자: 이승범
    *   작성내용: 회원가입 과정에서 필요한 센터정보 가져오기
    */
    public Slice<CenterInfoDto> findCenterForSignup(CenterInfoRequest request, Pageable pageable) {
       return centerRepository.findForSignup(request.getSido(), request.getSigungu(), request.getCenterName(), pageable);
    }

    /**
    *   작성날짜: 2022/06/24 10:31 AM
    *   작성자: 이승범
    *   작성내용: 아이추가 과정에서 필요한 센터정보 가져오기
    */
    public Slice<CenterInfoDto> findCenterForAddChild(CenterInfoRequest request, Pageable pageable) {
        return centerRepository.findCenterForAddChild(request.getSido(), request.getSigungu(), request.getCenterName(), pageable);
    }

    /**
     *   작성날짜: 2022/07/04 3:04 PM
     *   작성자: 이승범
     *   작성내용: 찜한 시설 리스트
     */
    public Slice<CenterPreview> findCentersByPrefer(Long userId, Pageable pageable) {
        return centerRepository.findByPrefer(userId, pageable);
    }
}
