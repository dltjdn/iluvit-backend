package FIS.iLUVit.service;

import FIS.iLUVit.dto.center.CenterBannerResponse;
import FIS.iLUVit.dto.center.CenterDetailRequest;
import FIS.iLUVit.dto.center.CenterRecommendDto;
import FIS.iLUVit.dto.center.CenterResponse;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Location;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.embeddable.Score;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.dto.center.CenterAndDistancePreviewDto;
import FIS.iLUVit.dto.center.CenterBannerDto;
import FIS.iLUVit.dto.center.CenterMapPreviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;
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
    private final MapService mapService;

    public List<CenterAndDistancePreviewDto> findByFilterForMapList(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance) {

        return centerRepository.findByFilterForMapList(longitude, latitude, theme, interestedAge, kindOf, distance);
    }

    public SliceImpl<CenterAndDistancePreviewDto> findByFilterForMapList(double longitude, double latitude, List<Long> centerIds, Long userId, KindOf kindOf, Pageable pageable) {
        return userId == null ?
                centerRepository.findByFilterForMapList(longitude, latitude, kindOf, centerIds, pageable) :
                centerRepository.findByFilterForMapList(longitude, latitude, userId, kindOf, centerIds, pageable);
    }

    public List<CenterMapPreviewDto> findByFilterForMap(double longitude, double latitude, Double distance, String searchContent){
        return centerRepository.findByFilterForMap(longitude, latitude, distance, searchContent);
    }


    public CenterResponse findInfoById(Long id) {
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new CenterException("해당 센터 존재하지 않음"));
        // Center 가 id 에 의해 조회 되었으므로 score에 1 추가
        center.addScore(Score.GET);
        CenterResponse result = new CenterResponse(center,imageService.getProfileImage(center),imageService.getInfoImages(center));
        return result;
    }

    public CenterBannerResponse findBannerById(Long id, Long userId) {
        CenterBannerDto data = userId == null ?
                centerRepository.findBannerById(id) :
                centerRepository.findBannerById(id, userId);

        if(data == null)
            return null;

        List<String> infoImages = imageService.getInfoImages(data.getInfoImages());
        return new CenterBannerResponse(data, infoImages);
    }

    public Long modifyCenterImage(Long centerId, Long userId, List<MultipartFile> infoImages, MultipartFile profileImage) {
        Teacher teacher = userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(centerId);
        // 해당하는 center 없으면 RuntimeException 반환
        Center center = teacher.getCenter();
        imageService.saveInfoImages(infoImages, center);
        imageService.saveProfileImage(profileImage, center);
        return center.getId();
    }

    public Long modifyCenterInfo(Long centerId, Long userId, CenterDetailRequest requestDto) {
        Teacher teacher = userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(centerId);
        // 해당하는 center 없으면 RuntimeException 반환
        Center center = teacher.getCenter();
        Pair<Double, Double> location = mapService.convertAddressToLocation(requestDto.getAddress());
        Pair<String, String> area = mapService.getSidoSigunguByLocation(location.getFirst(), location.getSecond());
        center.update(requestDto, location.getFirst(), location.getSecond(), area.getFirst(), area.getSecond());
        return center.getId();
    }

    public List<CenterRecommendDto> findCenterForParent(Long userId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException("해당 유저가 존재 하지 않습니다."));
        Theme theme = parent.getTheme();
        Location location = parent.getLocation();
        return centerRepository.findRecommendCenter(theme, location, PageRequest.of(0, 10, Sort.by("score")));
    }


}
