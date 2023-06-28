package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.dto.center.CenterBannerResponse;
import FIS.iLUVit.dto.center.CenterDetailRequest;
import FIS.iLUVit.dto.center.CenterRecommendDto;
import FIS.iLUVit.dto.center.CenterResponse;
import FIS.iLUVit.domain.embeddable.Score;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.exception.CenterErrorResult;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.dto.center.CenterAndDistancePreviewDto;
import FIS.iLUVit.dto.center.CenterMapPreviewDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import static FIS.iLUVit.domain.QCenter.center;
import static com.querydsl.core.types.dsl.MathExpressions.*;
import static com.querydsl.core.types.dsl.MathExpressions.radians;

@Service
@Transactional
@RequiredArgsConstructor
public class CenterService {

    private final CenterRepository centerRepository;
    private final ImageService imageService;

  private final ReviewRepository reviewRepository;
  private final CenterBookmarkRepository centerBookmarkRepository;

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final MapService mapService;

    public SliceImpl<CenterAndDistancePreviewDto> findByFilterForMapList(double longitude, double latitude, List<Long> centerIds, Long userId, KindOf kindOf, Pageable pageable) {

        return centerRepository.findByFilterForMapList(longitude, latitude, userId, kindOf, centerIds, pageable);
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

    public CenterBannerResponse findBannerById(Long centerId, Long userId) {

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

    public List<CenterRecommendDto> findCenterForParent(Long userId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException("해당 유저가 존재 하지 않습니다."));
        Theme theme = parent.getTheme();
        Location location = parent.getLocation();
        return centerRepository.findRecommendCenter(theme, location, PageRequest.of(0, 10, Sort.by("score")));
    }


}
