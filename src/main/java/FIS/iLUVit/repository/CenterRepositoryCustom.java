package FIS.iLUVit.repository;

import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRecommendDto;
import FIS.iLUVit.domain.Location;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.dto.center.CenterAndDistancePreviewDto;
import FIS.iLUVit.dto.center.CenterMapPreviewDto;
import FIS.iLUVit.dto.center.CenterPreviewDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public interface CenterRepositoryCustom {

    Slice<CenterPreviewDto> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable);

    List<CenterAndDistancePreviewDto> findByFilterForMapList(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance);

    SliceImpl<CenterAndDistancePreviewDto> findByFilterForMapList(double longitude, double latitude, Long userId, KindOf kindOf, List<Long> centerIds, Pageable pageable);

    SliceImpl<CenterAndDistancePreviewDto> findByFilterForMapList(double longitude, double latitude, KindOf kindOf, List<Long> centerIds, Pageable pageable);

    List<CenterMapPreviewDto> findByFilterForMap(double longitude, double latitude, Double distance, String searchContent);

    List<CenterRecommendDto> findRecommendCenter(Theme theme, Location location, Pageable pageable);

    Slice<CenterDto> findForSignup(String sido, String sigungu, String centerName, Pageable pageable);

    Slice<CenterDto> findCenterForAddChild(String sido, String sigungu, String centerName, Pageable pageable);

    Slice<CenterPreviewDto> findByPrefer(Long userId, Pageable pageable);
}
