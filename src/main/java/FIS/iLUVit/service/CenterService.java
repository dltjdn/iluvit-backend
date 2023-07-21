package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Location;
import FIS.iLUVit.dto.center.*;
import FIS.iLUVit.domain.embeddable.Score;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CenterService {

    private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (단위: km)
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
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        double longitude = centerSearchMapFilterDto.getLongitude();
        double latitude = centerSearchMapFilterDto.getLatitude();
        List<Long> centerIds = centerSearchMapFilterDto.getCenterIds();
        KindOf kindOf = centerSearchMapFilterDto.getKindOf();

        List<Center> centerByFilter = null;
        if(kindOf.equals(KindOf.ALL)){
            centerByFilter = centerRepository.findByIdInOrderByScoreDescIdAsc(centerIds);
        }else{
            centerByFilter = centerRepository.findByIdInAndKindOfOrderByScoreDescIdAsc(centerIds, kindOf);
        }

        List<CenterAndDistancePreviewDto> centerAndDistancePreviewDtos = new ArrayList<>();

        centerByFilter.forEach((center -> {

            double avgScore = reviewRepository.findByCenter(center).stream()
                    .mapToDouble(Review::getScore)
                    .average()
                    .orElse(0.0);// 또는 null

            // 해당 유저 아이디가 센터북마크에 있는지 검증하는 로직
            Optional<Prefer> prefer = centerBookmarkRepository.findByCenterAndParent(center, parent);

            double distance = calculateDistance(latitude, longitude, center.getLatitude(), center.getLongitude());

            CenterAndDistancePreviewDto centerAndDistancePreviewDto = new CenterAndDistancePreviewDto(center, distance, avgScore, prefer.isPresent());
            centerAndDistancePreviewDtos.add(centerAndDistancePreviewDto);

        }));

        boolean hasNext = false;

        if (centerAndDistancePreviewDtos.size() > pageable.getPageSize()) {
            hasNext = true;
            centerAndDistancePreviewDtos.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(centerAndDistancePreviewDtos, pageable, hasNext);

        //return centerRepository.findByFilterForMapList(longitude, latitude, userId, kindOf, centerIds, pageable);
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
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        // 리뷰 score 평균
        Double tempStarAvg = reviewRepository.findByCenter(center).stream()
                .mapToInt(Review::getScore).average().orElse(0.0);
        Double starAvg = Math.round(tempStarAvg * 10) / 10.0;

        // 현재 유저와 센터에 해당하는 북마크가 있을 시 센터 북마크 조회, 없을시 null
        Prefer centerBookmark = centerBookmarkRepository.findByCenterAndParent(center, parent)
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
    public void modifyCenterImage(Long centerId, Long userId, CenterImageRequest centerImageRequest) {
        Teacher teacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(centerId);

        // 해당하는 center 없으면 RuntimeException 반환
        Center center = teacher.getCenter();
        List<MultipartFile> infoImages = centerImageRequest.getInfoImages();
        MultipartFile profileImage = centerImageRequest.getProfileImage();

        imageService.saveInfoImages(infoImages, center);
        imageService.saveProfileImage(profileImage, center);
    }

    /**
     * 시설 정보 수정
     */
    public void modifyCenterInfo(Long userId, Long centerId, CenterDetailRequest centerDetailRequest) {
        Teacher teacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(centerId);
        // 해당하는 center 없으면 RuntimeException 반환
        Center center = teacher.getCenter();
        Pair<Double, Double> location = mapService.convertAddressToLocation(centerDetailRequest.getAddress());
        Pair<String, String> area = mapService.getSidoSigunguByLocation(location.getFirst(), location.getSecond());
        center.update(centerDetailRequest, location.getFirst(), location.getSecond(), area.getFirst(), area.getSecond());
    }

    /**
     * 두 지점 사이의 거리를 계산하는 메서드
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

}
