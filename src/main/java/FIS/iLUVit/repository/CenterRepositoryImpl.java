package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;

import java.util.List;

import static FIS.iLUVit.domain.QCenter.center;

@AllArgsConstructor
public class CenterRepositoryImpl extends CenterQueryMethod implements CenterRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Center> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, String kindOf, Integer offset, Integer limit) {
        return jpaQueryFactory.selectFrom(center)
                .where(areasIn(areas)
                        .and(center.theme.eq(theme))
                        .and(interestedAgeEq(interestedAge))
                        .and(kindOfEq(kindOf)))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<CenterAndDistance> findByMapFilter(double longitude, double latitude, Theme theme, Integer interestedAge, String kindOf, Integer distance) {
        double latitude_l = latitude - 0.009 * distance;
        double latitude_h = latitude + 0.009 * distance;
        double longitude_l = longitude - 0.009 * distance;
        double longitude_h = longitude + 0.009 * distance;

        return jpaQueryFactory.select(Projections.constructor(CenterAndDistance.class,
                center.id,
                center.name,
                center.owner,
                center.director,
                center.estType,
                center.tel,
                center.startTime,
                center.endTime,
                center.minAge,
                center.maxAge,
                center.address,
                center.area,
                center.longitude,
                center.latitude))
                .from(center)
                .where(center.latitude.between(latitude_l, latitude_h)
                        .and(center.longitude.between(longitude_l, longitude_h))
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge))
                        .and(kindOfEq(kindOf)))
                .fetch();
    }

}
