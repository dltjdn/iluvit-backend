package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.CenterInfoDto;
import FIS.iLUVit.controller.dto.CenterRecommendDto;
import FIS.iLUVit.controller.dto.QCenterInfoDto;
import FIS.iLUVit.controller.dto.QCenterRecommendDto;
import FIS.iLUVit.domain.Location;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.stream.Collectors;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPrefer.prefer;
import static FIS.iLUVit.domain.QReview.review;
import static com.querydsl.core.types.dsl.MathExpressions.*;

@AllArgsConstructor
public class CenterRepositoryImpl extends CenterQueryMethod implements CenterRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable) {
        List<CenterPreview> content = jpaQueryFactory.select(new QCenterPreview(center, review.score.avg()))
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
    public List<CenterAndDistancePreview> findByFilterForMapList(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance) {
        double latitude_l = latitude - 0.009 * distance;
        double latitude_h = latitude + 0.009 * distance;
        double longitude_l = longitude - 0.009 * distance;
        double longitude_h = longitude + 0.009 * distance;
        Expression<Double> latitudeEx = Expressions.constant(latitude);
        Expression<Double> longitudeEx = Expressions.constant(longitude);
        Expression<Double> param = Expressions.constant(6371.0);
        NumberExpression<Double> distanceEx = acos(cos(radians(latitudeEx)).multiply(cos(radians((center.latitude))))
                .multiply(cos(radians(center.longitude).subtract(radians(longitudeEx))))
                .add(sin(radians(longitudeEx)).multiply(sin(radians(center.longitude))))).multiply(param).as("distance");

        List<CenterAndDistancePreview> result = jpaQueryFactory.select(new QCenterAndDistancePreview(center, review.score.avg(), distanceEx))
                .from(center)
                .leftJoin(center.reviews, review)
                .where(center.latitude.between(latitude_l, latitude_h)
                        .and(center.longitude.between(longitude_l, longitude_h))
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge))
                        .and(kindOfEq(kindOf)))
                .groupBy(center)
                .fetch();

        return result.stream()
                .filter(centerAndDistancePreview ->
                        centerAndDistancePreview.calculateDistance(longitude, latitude) < distance)
                .collect(Collectors.toList());
    }

    @Override
    public SliceImpl<CenterAndDistancePreview> findByFilterForMapList(double longitude, double latitude, Long userId, KindOf kindOf, List<Long> centerIds, Pageable pageable) {
        Expression<Double> latitudeEx = Expressions.constant(latitude);
        Expression<Double> longitudeEx = Expressions.constant(longitude);
        Expression<Double> param = Expressions.constant(6371.0);

        NumberExpression<Double> distanceEx = acos(
                sin(radians(latitudeEx)).multiply(sin(radians(center.latitude)))
                        .add(cos(radians(latitudeEx)).multiply(cos(radians(center.latitude)))
                                .multiply(cos(radians(longitudeEx).subtract(radians(center.longitude)))))).multiply(param);

        List<CenterAndDistancePreview> result =
                jpaQueryFactory.select(
                                new QCenterAndDistancePreview(
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
    public SliceImpl<CenterAndDistancePreview> findByFilterForMapList(double longitude, double latitude, KindOf kindOf, List<Long> centerIds, Pageable pageable) {

        Expression<Double> latitudeEx = Expressions.constant(latitude);
        Expression<Double> longitudeEx = Expressions.constant(longitude);
        Expression<Double> param = Expressions.constant(6371.0);

        NumberExpression<Double> distanceEx = acos(
                sin(radians(latitudeEx)).multiply(sin(radians(center.latitude)))
                        .add(cos(radians(latitudeEx)).multiply(cos(radians(center.latitude)))
                                .multiply(cos(radians(longitudeEx).subtract(radians(center.longitude)))))).multiply(param);

        List<CenterAndDistancePreview> result =
                jpaQueryFactory.select(
                                new QCenterAndDistancePreview(
                                        distanceEx,
                                        center.id, center.name, center.kindOf, center.estType, center.tel, center.startTime, center.endTime, center.minAge, center.maxAge, center.address, center.addressDetail, center.longitude, center.latitude, center.theme,
                                        review.score.avg(),
                                        center.profileImagePath
                                ))
                        .from(center)
                        .leftJoin(center.reviews, review)
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
    public List<CenterMapPreview> findByFilterForMap(double longitude, double latitude, Double distance, String searchContent) {

        Expression<Double> latitudeEx = Expressions.constant(latitude);
        Expression<Double> longitudeEx = Expressions.constant(longitude);
        Expression<Double> param = Expressions.constant(6371.0);

        NumberExpression<Double> distanceEx = acos(
                sin(radians(latitudeEx)).multiply(sin(radians(center.latitude)))
                        .add(cos(radians(latitudeEx)).multiply(cos(radians(center.latitude)))
                                .multiply(cos(radians(longitudeEx).subtract(radians(center.longitude)))))).multiply(param);

        List<CenterMapPreview> result = jpaQueryFactory.select(new QCenterMapPreview(center.id, center.name, center.longitude, center.latitude))
                .from(center)
                .leftJoin(center.reviews, review)
                .groupBy(center)
                .where(centerNameEq(searchContent), kindOfEq(KindOf.Kindergarten), kindOfEq(KindOf.ChildHouse))
                .having(distanceEx.loe(distance))
                .orderBy(center.score.desc())
                .limit(100)
                .fetch();


        while (result.size() <= 10 && searchContent != null && !searchContent.equals("") && distance <= 1600) {
            distance = distance * 3;
            result = jpaQueryFactory.select(new QCenterMapPreview(center.id, center.name, center.longitude, center.latitude))
                    .from(center)
                    .leftJoin(center.reviews, review)
                    .groupBy(center)
                    .where(centerNameEq(searchContent))
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
    public Slice<CenterInfoDto> findForSignup(String sido, String sigungu, String centerName, Pageable pageable) {
        List<CenterInfoDto> content = jpaQueryFactory.select(new QCenterInfoDto(center.id, center.name, center.address))
                .from(center)
                .where(areaEq(sido, sigungu)
                        ,(centerNameEq(centerName))
                        ,(center.kindOf.isNotNull()))
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
    public Slice<CenterInfoDto> findCenterForAddChild(String sido, String sigungu, String centerName, Pageable pageable) {
        List<CenterInfoDto> content = jpaQueryFactory.select(new QCenterInfoDto(center.id, center.name, center.address))
                .from(center)
                .where(center.signed.eq(true)
                        , (areaEq(sido, sigungu))
                        , (centerNameEq(centerName))
                        , (center.kindOf.isNotNull()))
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
    public Slice<CenterPreview> findByPrefer(Long userId, Pageable pageable) {
        List<CenterPreview> content = jpaQueryFactory.select(new QCenterPreview(center, review.score.avg()))
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
