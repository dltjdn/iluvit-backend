package FIS.iLUVit.repository;

import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import FIS.iLUVit.repository.dto.QCenterAndDistancePreview;
import FIS.iLUVit.repository.dto.QCenterPreview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;

import java.util.List;

import static FIS.iLUVit.domain.QCenter.center;

@AllArgsConstructor
public class CenterRepositoryImpl extends CenterQueryMethod implements CenterRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, String kindOf, Integer offset, Integer limit) {
        return jpaQueryFactory.select(new QCenterPreview(center))
                .from(center)
                .where(kindOfEq(kindOf)
                        .and(areasIn(areas))
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge)))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<CenterAndDistancePreview> findByMapFilter(double longitude, double latitude, Theme theme, Integer interestedAge, String kindOf, Integer distance) {
        double latitude_l = latitude - 0.009 * distance;
        double latitude_h = latitude + 0.009 * distance;
        double longitude_l = longitude - 0.009 * distance;
        double longitude_h = longitude + 0.009 * distance;

        return jpaQueryFactory.select(new QCenterAndDistancePreview(center))
                .from(center)
                .where(center.latitude.between(latitude_l, latitude_h)
                        .and(center.longitude.between(longitude_l, longitude_h))
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge))
                        .and(kindOfEq(kindOf)))
                .fetch();
    }

}
