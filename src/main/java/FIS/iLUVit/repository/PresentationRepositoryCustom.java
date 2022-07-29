package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.PresentationPreviewForUsersResponse;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public interface PresentationRepositoryCustom {

    SliceImpl<PresentationPreviewForUsersResponse> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, String searchContent, Pageable pageable);

}
