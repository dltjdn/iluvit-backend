package FIS.iLUVit.repository;

import FIS.iLUVit.dto.center.*;
import FIS.iLUVit.domain.Location;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
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
import static com.querydsl.core.types.dsl.MathExpressions.*;

@AllArgsConstructor
public class CenterRepositoryImpl extends CenterQueryMethod implements CenterRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /*
        게시글 리스트를 조회합니다.
     */
    @Override
    public Slice<CenterPreviewDto> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable) {
        List<CenterPreviewDto> content = jpaQueryFactory.select(new QCenterPreviewDto(center, review.score.avg()))
                .from(center)
                .leftJoin(center.reviews, review)
                .where(areasIn(areas)
                        .and(kindOfEq(kindOf))
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge)))
                .orderBy(center.score.desc(), center.id.asc())
                .groupBy(center)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public SliceImpl<CenterAndDistancePreviewDto> findByFilterForMapList(double longitude, double latitude, Long userId, KindOf kindOf, List<Long> centerIds, Pageable pageable) {
        Expression<Double> latitudeEx = Expressions.constant(latitude);
        Expression<Double> longitudeEx = Expressions.constant(longitude);
        Expression<Double> param = Expressions.constant(6371.0);

        NumberExpression<Double> distanceEx = acos(
                sin(radians(latitudeEx)).multiply(sin(radians(center.latitude)))
                        .add(cos(radians(latitudeEx)).multiply(cos(radians(center.latitude)))
                                .multiply(cos(radians(longitudeEx).subtract(radians(center.longitude)))))).multiply(param);

        List<CenterAndDistancePreviewDto> result =
                jpaQueryFactory.select(
                                new QCenterAndDistancePreviewDto(
                                        distanceEx,
                                        center.id, center.name, center.kindOf, center.estType, center.tel, center.startTime, center.endTime, center.minAge, center.maxAge, center.address, center.addressDetail, center.longitude, center.latitude, center.theme,
                                        review.score.avg(),
                                        center.profileImagePath, prefer.parent.id
                                ))
                        .from(center)
                        .leftJoin(center.reviews, review)
                        .leftJoin(center.prefers, prefer).on(prefer.parent.id.eq(userId))
                        .where(kindOfEq(kindOf), center.id.in(centerIds))
                        .groupBy(center)
                        .orderBy(center.score.desc(), center.id.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize() + 1)
                        .fetch();

        boolean hasNext = false;
        if (result.size() > pageable.getPageSize()) {
            hasNext = true;
            result.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(result, pageable, hasNext);

    }

    @Override
    public List<CenterMapPreviewDto> findByFilterForMap(double longitude, double latitude, Double distance, String searchContent) {

        Expression<Double> latitudeEx = Expressions.constant(latitude);
        Expression<Double> longitudeEx = Expressions.constant(longitude);
        Expression<Double> param = Expressions.constant(6371.0);

        NumberExpression<Double> distanceEx = acos(
                sin(radians(latitudeEx)).multiply(sin(radians(center.latitude)))
                        .add(cos(radians(latitudeEx)).multiply(cos(radians(center.latitude)))
                                .multiply(cos(radians(longitudeEx).subtract(radians(center.longitude)))))).multiply(param);

        List<CenterMapPreviewDto> result = jpaQueryFactory.select(new QCenterMapPreviewDto(center.id, center.name, center.longitude, center.latitude))
                .from(center)
                .leftJoin(center.reviews, review)
                .groupBy(center)
                .where(centerNameEq(searchContent)
                        , (kindOfEq(KindOf.Kindergarten).or(kindOfEq(KindOf.ChildHouse))))
                .having(distanceEx.loe(distance))
                .orderBy(center.score.desc())
                .limit(100)
                .fetch();


        while (result.size() <= 10 && searchContent != null && !searchContent.equals("") && distance <= 1600) {
            distance = distance * 3;
            result = jpaQueryFactory.select(new QCenterMapPreviewDto(center.id, center.name, center.longitude, center.latitude))
                    .from(center)
                    .leftJoin(center.reviews, review)
                    .groupBy(center)
                    .where(centerNameEq(searchContent)
                            , (kindOfEq(KindOf.Kindergarten).or(kindOfEq(KindOf.ChildHouse))))
                    .having(distanceEx.loe(distance))
                    .orderBy(center.score.desc())
                    .limit(100)
                    .fetch();
        }

        return result;
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
