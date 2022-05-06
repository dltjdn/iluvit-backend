package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
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
    public List<Center> findByMapFilter(double longitude, double latitude, Theme theme, Integer interestedAge, String kindOf, Integer distance) {
        return jpaQueryFactory.selectFrom(center)
                .where(center.theme.eq(theme)
                        .and(interestedAgeEq(interestedAge))
                        .and(kindOfEq(kindOf)))
                .fetch();
    }

}
