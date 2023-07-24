package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.dto.center.*;
import FIS.iLUVit.domain.embeddable.Location;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import java.util.List;
import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPrefer.prefer;
import static FIS.iLUVit.domain.QReview.review;


@AllArgsConstructor
public class CenterRepositoryImpl extends CenterQueryMethod implements CenterRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 일정 거리 내의 시설 전체 조회 + 검색어 있으면 검색어에 해당하는 시설 조회
     */
    @Override
    public List<Center> findByFilterForMap(double longitude, double latitude, Double distance, String searchContent) {

        NumberExpression distanceEx = distanceRange(longitude, latitude); // 센터와 나의 위치간의 거리 계산
        BooleanExpression namePredicate = center.name.contains(searchContent);
        BooleanExpression kindOfPredicate = center.kindOf.eq(KindOf.ChildHouse).or(center.kindOf.eq(KindOf.Kindergarten));

        List<Center> centers;

        while (true) {
            centers = jpaQueryFactory.select(center)
                    .from(center)
                    .groupBy(center)
                    .where(namePredicate.and(kindOfPredicate))
                    .having(distanceEx.loe(distance))
                    .orderBy(center.score.desc())
                    .limit(100)
                    .fetch();

            if(centers.size() > 10 || searchContent == null || searchContent.equals("") || distance > 1600) break;

            distance = distance * 3;
        }

        return centers;
    }

    /**
     *  해당 시도, 시군구에서 학부모가 선택한 관심 테마를 가지고 있는 시설 조회
     */
    @Override
    public List<Center> findRecommendCenter(Theme theme, Location location, Pageable pageable) {
        List<Center> centers = jpaQueryFactory.select(center)
                .from(center)
                .where(areaEq(location.getSido(), location.getSigungu()), themeEq(theme))
                .orderBy(center.score.desc(), center.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int size = centers.size();
        if (size < 10) {
            List<Center> temp = jpaQueryFactory.select(center)
                    .from(center)
                    .where(areaEq(location.getSido(), location.getSigungu()), center.theme.isNull())
                    .orderBy(center.score.desc(), center.id.asc())
                    .limit(10 - size)
                    .fetch();
            centers.addAll(temp);
        }
        return centers;
    }

    /**
     * 회원가입 과정에서 시설정보 가져오기
     */
    @Override
    public List<Center> findForSignup(String sido, String sigungu, String centerName) {
        List<Center> centers = jpaQueryFactory.select(center)
                .from(center)
                .where(areaEq(sido, sigungu), (centerNameEq(centerName))
                        , (kindOfEq(KindOf.Kindergarten).or(kindOfEq(KindOf.ChildHouse))))
                .fetch();
        return centers;
    }

    /**
     * 아이추가 과정에서 필요한 센터정보 가져오기
     */
    @Override
    public List<Center> findCenterForAddChild(String sido, String sigungu, String centerName) {
        List<Center> centers = jpaQueryFactory.select(center)
                .from(center)
                .where(center.signed.eq(true)
                        , (areaEq(sido, sigungu))
                        , (centerNameEq(centerName))
                        , (kindOfEq(KindOf.Kindergarten).or(kindOfEq(KindOf.ChildHouse))))
                .fetch();

        return centers;
    }

}
