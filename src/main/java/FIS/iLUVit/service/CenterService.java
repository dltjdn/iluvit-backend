package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Location;
import FIS.iLUVit.dto.center.*;
import FIS.iLUVit.domain.embeddable.Score;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.exception.CenterErrorResult;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
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
    private final ReviewRepository reviewRepository;
    private final CenterBookmarkRepository centerBookmarkRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final MapService mapService;


    /**
     * 시설 전체 조회
     */
    public List<CenterMapPreviewDto> findCenterByFilterForMap(CenterSearchMapDto centerSearchMapDto){
        double longitude = centerSearchMapDto.getLongitude();
        double latitude = centerSearchMapDto.getLatitude();
        Double distance = centerSearchMapDto.getDistance();
        String searchContent = centerSearchMapDto.getSearchContent();


        return centerRepository.findByFilterForMap(longitude, latitude, distance, searchContent);
    }

    /**
     * 유저가 설정한 필터 기반 시설 조회
     */
    public SliceImpl<CenterAndDistancePreviewDto> findCenterByFilterForMapList(long userId,  CenterSearchMapFilterDto centerSearchMapFilterDto, Pageable pageable) {
        double longitude = centerSearchMapFilterDto.getLongitude();
        double latitude = centerSearchMapFilterDto.getLatitude();
        List<Long> centerIds = centerSearchMapFilterDto.getCenterIds();
        KindOf kindOf = centerSearchMapFilterDto.getKindOf();

        return centerRepository.findByFilterForMapList(longitude, latitude, userId, kindOf, centerIds, pageable);
    }

    /**
     * 시설 상세 조회
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
     * 미리보기 배너 용 시설 상세 조회
     */
    public CenterBannerResponse findCenterBannerByCenter(Long userId, Long centerId) {

        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        // 리뷰 score 평균
        Double tempStarAvg = reviewRepository.findByCenter(center).stream()
                .mapToInt(Review::getScore).average().orElse(0.0);
        Double starAvg = Math.round(tempStarAvg * 10) / 10.0;

        // 현재 유저와 센터에 해당하는 북마크가 있을 시 센터 북마크 조회, 없을시 null
        Prefer centerBookmark = centerBookmarkRepository.findByCenterAndParentId(center, userId)
                .orElse(null);


        List<String> infoImages = imageService.getInfoImages(center.getInfoImagePath());

        return new CenterBannerResponse(center.getId(),center.getName(), center.getSigned(), center.getRecruit(),centerBookmark, center.getProfileImagePath(), starAvg, infoImages);

    }

    /**
     *  추천 시설 전체 조회 ( 학부모가 선택한 관심 테마를 가지고 있는 시설 조회 )
     */
    public List<CenterRecommendDto> findRecommendCenterWithTheme(Long userId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException("해당 유저가 존재 하지 않습니다."));
        Theme theme = parent.getTheme();
        Location location = parent.getLocation();
        return centerRepository.findRecommendCenter(theme, location, PageRequest.of(0, 10, Sort.by("score")));
    }

    /**
     * 시설 이미지 수정
     */
    public void modifyCenterImage(Long centerId, Long userId, List<MultipartFile> infoImages, MultipartFile profileImage) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        Teacher teacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(centerId);
        // 해당하는 center 없으면 RuntimeException 반환
        Center center = teacher.getCenter();
        imageService.saveInfoImages(infoImages, center);
        imageService.saveProfileImage(profileImage, center);
    }

    /**
     * 시설 정보 수정
     */
    public void modifyCenterInfo(Long userId, Long centerId, CenterDetailRequest centerDetailRequest) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        Teacher teacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(centerId);
        // 해당하는 center 없으면 RuntimeException 반환
        Center center = teacher.getCenter();
        Pair<Double, Double> location = mapService.convertAddressToLocation(centerDetailRequest.getAddress());
        Pair<String, String> area = mapService.getSidoSigunguByLocation(location.getFirst(), location.getSecond());
        center.update(centerDetailRequest, location.getFirst(), location.getSecond(), area.getFirst(), area.getSecond());
    }

}
