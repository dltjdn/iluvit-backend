package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.CenterInfoDto;
import FIS.iLUVit.controller.dto.CenterRecommendDto;
import FIS.iLUVit.domain.Location;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterMapPreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public interface CenterRepositoryCustom {

    Slice<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable);

    List<CenterAndDistancePreview> findByFilterForMapList(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance);

    SliceImpl<CenterAndDistancePreview> findByFilterForMapList(double longitude, double latitude, Long userId, KindOf kindOf, List<Long> centerIds, Pageable pageable);

    SliceImpl<CenterAndDistancePreview> findByFilterForMapList(double longitude, double latitude, KindOf kindOf, List<Long> centerIds, Pageable pageable);

    List<CenterMapPreview> findByFilterForMap(double longitude, double latitude, Double distance, String searchContent);

    List<CenterRecommendDto> findRecommendCenter(Theme theme, Location location, Pageable pageable);

    Slice<CenterInfoDto> findForSignup(String sido, String sigungu, String centerName, Pageable pageable);

    Slice<CenterInfoDto> findCenterForAddChild(String sido, String sigungu, String centerName, Pageable pageable);

    Slice<CenterPreview> findByPrefer(Long userId, Pageable pageable);
}
