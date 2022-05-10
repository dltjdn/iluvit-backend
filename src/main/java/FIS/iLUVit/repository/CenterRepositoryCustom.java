package FIS.iLUVit.repository;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;

import java.util.List;

public interface CenterRepositoryCustom {

    public List<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, String kindOf, Integer offset, Integer limit);

    List<CenterAndDistancePreview> findByMapFilter(double longitude, double latitude, Theme theme, Integer interestedAge, String kindOf, Integer distance);
}
