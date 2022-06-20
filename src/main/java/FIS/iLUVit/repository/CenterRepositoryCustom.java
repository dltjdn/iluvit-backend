package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.CenterInfoForSignupDto;
import FIS.iLUVit.controller.dto.CenterInfoForSignupRequest;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CenterRepositoryCustom {

    Slice<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable);

    List<CenterAndDistancePreview> findByMapFilter(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance);

    List<Long> findByThemeAndAgeOnly3(Theme theme, Pageable pageable);

    Slice<CenterInfoForSignupDto> findForSignup(CenterInfoForSignupRequest request, Pageable pageable);
}
