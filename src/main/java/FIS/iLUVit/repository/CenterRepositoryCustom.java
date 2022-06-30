package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.CenterInfoDto;
import FIS.iLUVit.controller.dto.CenterInfoRequest;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CenterRepositoryCustom {

    Slice<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable);

    List<CenterAndDistancePreview> findByMapFilter(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance);

    List<Long> findByThemeAndAgeOnly3(Theme theme, Pageable pageable);

    Slice<CenterInfoDto> findForSignup(String sido, String sigungu, String centerName, Pageable pageable);

    Slice<CenterInfoDto> findCenterForAddChild(String sido, String sigungu, String centerName, Pageable pageable);
}
