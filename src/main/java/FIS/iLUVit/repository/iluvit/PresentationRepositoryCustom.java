package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.dto.presentation.PresentationForUserResponse;
import FIS.iLUVit.domain.common.embeddable.Area;
import FIS.iLUVit.domain.common.embeddable.Theme;
import FIS.iLUVit.domain.iluvit.enumtype.KindOf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public interface PresentationRepositoryCustom {

    SliceImpl<PresentationForUserResponse> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, String searchContent, Pageable pageable);

}
