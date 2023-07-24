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


    @Override
    public List<CenterRecommendDto> findRecommendCenter(Theme theme, Location location, Pageable pageable) {
        List<CenterRecommendDto> result = jpaQueryFactory.select(new QCenterRecommendDto(center.id, center.name, center.profileImagePath))
                .from(center)
                .where(areaEq(location.getSido(), location.getSigungu()), themeEq(theme))
                .orderBy(center.score.desc(), center.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int size = result.size();
        if (size < 10) {
            List<CenterRecommendDto> temp = jpaQueryFactory.select(new QCenterRecommendDto(center.id, center.name, center.profileImagePath))
                    .from(center)
                    .where(areaEq(location.getSido(), location.getSigungu()), center.theme.isNull())
                    .orderBy(center.score.desc(), center.id.asc())
                    .limit(10 - size)
                    .fetch();
            result.addAll(temp);
        }
        return result;
    }

    /**
     * 작성날짜: 2022/08/24 5:17 PM
     * 작성자: 이승범
     * 작성내용: 회원가입 과정에서 시설정보 가져오기
     */
    @Override
    public Slice<CenterDto> findForSignup(String sido, String sigungu, String centerName, Pageable pageable) {
        List<CenterDto> content = jpaQueryFactory.select(new QCenterDto(center.id, center.name, center.address))
                .from(center)
                .where(areaEq(sido, sigungu)
                        , (centerNameEq(centerName))
                        , (kindOfEq(KindOf.Kindergarten).or(kindOfEq(KindOf.ChildHouse))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    /**
     * 작성날짜: 2022/08/24 5:17 PM
     * 작성자: 이승범
     * 작성내용: 아이추가 과정에서 필요한 센터정보 가져오기
     */
    @Override
    public Slice<CenterDto> findCenterForAddChild(String sido, String sigungu, String centerName, Pageable pageable) {
        List<CenterDto> content = jpaQueryFactory.select(new QCenterDto(center.id, center.name, center.address))
                .from(center)
                .where(center.signed.eq(true)
                        , (areaEq(sido, sigungu))
                        , (centerNameEq(centerName))
                        , (kindOfEq(KindOf.Kindergarten).or(kindOfEq(KindOf.ChildHouse))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    /**
     * 작성날짜: 2022/08/14 5:17 PM
     * 작성자: 이승범
     * 작성내용: 찜한 시설 가져오기
     */
    @Override
    public Slice<CenterPreviewDto> findByPrefer(Long userId, Pageable pageable) {
        List<CenterPreviewDto> content = jpaQueryFactory.select(new QCenterPreviewDto(center, review.score.avg()))
                .from(center)
                .join(center.prefers, prefer).on(prefer.parent.id.eq(userId))
                .leftJoin(center.reviews, review)
                .groupBy(center)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

}
