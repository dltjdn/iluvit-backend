package FIS.iLUVit.domain.center.service;

import FIS.iLUVit.domain.center.dto.*;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.centerbookmark.domain.Prefer;
import FIS.iLUVit.domain.centerbookmark.repository.CenterBookmarkRepository;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.parent.repository.ParentRepository;
import FIS.iLUVit.domain.review.domain.Review;
import FIS.iLUVit.domain.review.repository.ReviewRepository;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;
import FIS.iLUVit.domain.common.domain.Location;
import FIS.iLUVit.domain.center.domain.Score;
import FIS.iLUVit.domain.center.domain.Theme;
import FIS.iLUVit.domain.center.domain.KindOf;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.common.service.ImageService;
import FIS.iLUVit.domain.common.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 주변 시설 전체 조회
     */
    public List<CenterMapResponse> findCenterByFilterForMap(String searchContent, CenterMapRequest centerMapRequest){
        double longitude = centerMapRequest.getLongitude();
        double latitude = centerMapRequest.getLatitude();
        Double distance = centerMapRequest.getDistance();

        List<Center> centerByFilter = centerRepository.findByFilterForMap(longitude, latitude, distance, searchContent);

        List<CenterMapResponse> centerMapResponses = centerByFilter.stream().map(center -> {
            return new CenterMapResponse(center.getId(), center.getName(), center.getLongitude(), center.getLatitude(), center.getSigned());
        }).collect(Collectors.toList());

        return centerMapResponses;
    }

    /**
     * 유저가 설정한 필터 기반 시설 조회
     */
    public SliceImpl<CenterMapFilterResponse> findCenterByFilterForMapList(long userId, KindOf kindOf, CenterMapFilterRequest centerMapFilterRequest, Pageable pageable) {
        Parent parent = getParent(userId);

        double longitude = centerMapFilterRequest.getLongitude();
        double latitude = centerMapFilterRequest.getLatitude();
        List<Long> centerIds = centerMapFilterRequest.getCenterIds();

        List<Center> centerByFilter = null;
        if(kindOf.equals(KindOf.ALL)){
            centerByFilter = centerRepository.findByIdInOrderByScoreDescIdAsc(centerIds);
        }else{
            centerByFilter = centerRepository.findByIdInAndKindOfOrderByScoreDescIdAsc(centerIds, kindOf);
        }

        List<CenterMapFilterResponse> centerMapFilterResponses = new ArrayList<>();

        centerByFilter.forEach((center -> {

            double avgScore = reviewRepository.findByCenter(center).stream()
                    .mapToDouble(Review::getScore)
                    .average()
                    .orElse(0.0);// 또는 null

            // 해당 유저 아이디가 센터북마크에 있는지 검증하는 로직
            Optional<Prefer> prefer = centerBookmarkRepository.findByCenterAndParent(center, parent);

            double distance = calculateDistance(latitude, longitude, center.getLatitude(), center.getLongitude());

            CenterMapFilterResponse centerMapFilterResponse = new CenterMapFilterResponse(center, distance, avgScore, prefer.isPresent());
            centerMapFilterResponses.add(centerMapFilterResponse);

        }));

        boolean hasNext = false;

        if (centerMapFilterResponses.size() > pageable.getPageSize()) {
            hasNext = true;
            centerMapFilterResponses.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(centerMapFilterResponses, pageable, hasNext);
    }


    /**
     * 시설 상세 조회
     */
    public CenterDetailResponse findCenterDetailsByCenter(Long centerId) {
        Center center = getCenter(centerId);

        // Center 가 id 에 의해 조회 되었으므로 score에 1 추가
        center.addScore(Score.GET);

        CenterDetailResponse centerDetailResponse = new CenterDetailResponse(center,center.getProfileImagePath(),imageService.getInfoImages(center.getInfoImagePath()));
        return centerDetailResponse;
    }

    /**
     * 미리보기 배너 용 시설 상세 조회
     */
    public CenterBannerResponse findCenterBannerByCenter(Long userId, Long centerId) {
        Parent parent = getParent(userId);
        Center center = getCenter(centerId);

        // 리뷰 score 평균
        Double tempStarAvg = reviewRepository.findByCenter(center).stream()
                .mapToInt(Review::getScore).average().orElse(0.0);
        Double starAvg = Math.round(tempStarAvg * 10) / 10.0;

        // 현재 유저와 센터에 해당하는 북마크가 있을 시 센터 북마크 조회, 없을시 null
        Optional<Prefer> centerBookmark = centerBookmarkRepository.findByCenterAndParent(center, parent);

        Boolean hasCenterBookmark = centerBookmark.isPresent();

        List<String> infoImages = imageService.getInfoImages(center.getInfoImagePath());

        CenterBannerResponse centerBannerResponse = new CenterBannerResponse(center, infoImages, hasCenterBookmark, starAvg);

        return centerBannerResponse;
    }

    /**
     *  추천 시설 전체 조회 ( 학부모가 선택한 관심 테마를 가지고 있는 시설 조회 )
     */
    public List<CenterRecommendResponse> findRecommendCenterWithTheme(Long userId) {
        Parent parent = getParent(userId);

        Theme theme = parent.getTheme();
        Location location = parent.getLocation();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("score"));


        List<CenterRecommendResponse> centerRecommendResponses = centerRepository.findRecommendCenter(theme, location, pageRequest).stream()
                .map(CenterRecommendResponse::new) // Center를 CenterRecommendDto로 변환
                .collect(Collectors.toList());

        return centerRecommendResponses;
    }

    /**
     * 시설 정보 수정
     */
    public void modifyCenterInfo(Long userId, Long centerId, CenterDetailRequest centerDetailRequest) {
        Teacher teacher = getTeacher(userId, centerId);
        // 해당하는 center 없으면 RuntimeException 반환

        Center center = teacher.getCenter();

        Pair<Double, Double> location = mapService.convertAddressToLocation(centerDetailRequest.getAddress());

        Double longitude = location.getFirst();
        Double latitude = location.getSecond();

        Pair<String, String> area = mapService.getSidoSigunguByLocation(longitude ,latitude);
        String sido = area.getFirst();
        String sigungu = area.getSecond();

        center.updateCenter(centerDetailRequest,longitude, latitude, sido, sigungu);
    }


    /**
     * 시설 이미지 수정
     */
    public void modifyCenterImage(Long userId, Long centerId, CenterImageRequest centerImageRequest) {
        Teacher teacher = getTeacher(userId, centerId);

        List<MultipartFile> infoImages = centerImageRequest.getInfoImages();
        MultipartFile profileImage = centerImageRequest.getProfileImage();

        imageService.saveInfoImages(infoImages, teacher.getCenter());
        imageService.saveProfileImage(profileImage, teacher.getCenter());
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

    /**
     * 예외처리 - 존재하는 권한있는 선생님인가
     */
    private Teacher getTeacher(Long userId, Long centerId) {
        Teacher teacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND))
                .checkPermission(centerId);
        return teacher;
    }

    /**
     * 예외처리 - 존재하는 학부모인가
     */
    private Parent getParent(long userId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
        return parent;
    }

    /**
     * 예외처리 - 존재하는 시설인가
     */
    private Center getCenter(Long centerId) {
        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
        return center;
    }

}
