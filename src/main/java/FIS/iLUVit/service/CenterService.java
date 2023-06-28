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

    /**
     * 작성자: 현승구
     * 작성내용: 위치에 따른 전체 시설을 리스트로 반환합니다
     */
    public List<CenterMapPreviewDto> findCenterByFilterForMap(double longitude, double latitude, Double distance, String searchContent){
        return centerRepository.findByFilterForMap(longitude, latitude, distance, searchContent);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 유저가 설정한 필터를 기반으로 시설을 조회합니다
     */
    public SliceImpl<CenterAndDistancePreviewDto> findCenterByFilterForMapList(double longitude, double latitude, List<Long> centerIds, Long userId, KindOf kindOf, Pageable pageable) {
        return userId == null ?
                centerRepository.findByFilterForMapList(longitude, latitude, kindOf, centerIds, pageable) :
                centerRepository.findByFilterForMapList(longitude, latitude, userId, kindOf, centerIds, pageable);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 시설별 정보를 상세 조회합니다
     */
    public CenterResponse findCenterDetailsByCenter(Long id) {
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new CenterException("해당 센터 존재하지 않음"));
        // Center 가 id 에 의해 조회 되었으므로 score에 1 추가
        center.addScore(Score.GET);
        CenterResponse result = new CenterResponse(center,imageService.getProfileImage(center),imageService.getInfoImages(center));

        return result;
    }

    /**
     * 작성자: 현승구
     * 작성내용: 시설별 정보 preview를 조회합니다
     */
    public CenterBannerResponse findCenterBannerByCenter(Long id, Long userId) {
        CenterBannerDto data = userId == null ?
                centerRepository.findBannerById(id) :
                centerRepository.findBannerById(id, userId);

        if(data == null)
            return null;

        Double starAverage = data.getStarAverage();
        if(data.getStarAverage() != null) {
            starAverage = (Math.round(starAverage * 10) / 10.0);
        }

        List<String> infoImages = imageService.getInfoImages(data.getInfoImages());
        return new CenterBannerResponse(data, starAverage, infoImages);
    }

    /**
     * 작성자: 현승구
     * 작성내용: 학부모가 선택한 관심 테마를 가지고 있는 시설을 조회합니다
     */
    public List<CenterRecommendDto> findRecommendCenterWithTheme(Long userId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException("해당 유저가 존재 하지 않습니다."));
        Theme theme = parent.getTheme();
        Location location = parent.getLocation();

        return centerRepository.findRecommendCenter(theme, location, PageRequest.of(0, 10, Sort.by("score")));
    }

    /**
     * 작성자: 현승구
     * 작성내용: 교사가 시설의 이미지를 수정합니다
     */
    public Long modifyCenterImage(Long centerId, Long userId, List<MultipartFile> infoImages, MultipartFile profileImage) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        Teacher teacher = userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(centerId);
        // 해당하는 center 없으면 RuntimeException 반환
        Center center = teacher.getCenter();
        imageService.saveInfoImages(infoImages, center);
        imageService.saveProfileImage(profileImage, center);

        return center.getId();
    }

    /**
     * 작성자: 현승구
     * 작성내용: 교사가 시설의 정보를 수정합니다
     */
    public Long modifyCenterInfo(Long centerId, Long userId, CenterDetailRequest requestDto) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

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

}
