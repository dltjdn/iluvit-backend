package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.CenterInfoDto;
import FIS.iLUVit.controller.dto.QCenterInfoDto;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import FIS.iLUVit.repository.dto.QCenterAndDistancePreview;
import FIS.iLUVit.repository.dto.QCenterPreview;
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
    public Slice<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable){
        List<CenterPreview> content = jpaQueryFactory.select(new QCenterPreview(center, review.score.avg()))
                .from(center)
                .leftJoin(center.reviews, review)
                .where(areasIn(areas)
                        .and(kindOfEq(kindOf))
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge)))
                .orderBy(center.score.asc(), center.id.desc())
                .groupBy(center)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if(content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<CenterAndDistancePreview> findByMapFilter(double longitude, double latitude, Theme theme, Integer interestedAge, KindOf kindOf, Integer distance) {
        double latitude_l = latitude - 0.009 * distance;
        double latitude_h = latitude + 0.009 * distance;
        double longitude_l = longitude - 0.009 * distance;
        double longitude_h = longitude + 0.009 * distance;

        return jpaQueryFactory.select(new QCenterAndDistancePreview(center, review.score.avg()))
                .from(center)
                .leftJoin(center.reviews, review)
                .where(center.latitude.between(latitude_l, latitude_h)
                        .and(center.longitude.between(longitude_l, longitude_h))
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge))
                        .and(kindOfEq(kindOf)))
                .groupBy(center)
                .fetch();
    }

    @Override
    public List<Long> findByThemeAndAgeOnly3(Theme theme, Pageable pageable) {
        return jpaQueryFactory.select(center.id)
                .from(center)
                .where(themeEq(theme))
                .orderBy(center.score.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Slice<CenterInfoDto> findForSignup(String sido, String sigungu, String centerName, Pageable pageable) {
        List<CenterInfoDto> content = jpaQueryFactory.select(new QCenterInfoDto(center.id, center.name, center.address))
                .from(center)
                .where(areaEq(sido, sigungu)
                        ,(centerNameEq(centerName)))
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

    @Override
    public Slice<CenterInfoDto> findCenterForAddChild(String sido, String sigungu, String centerName, Pageable pageable) {
        List<CenterInfoDto> content = jpaQueryFactory.select(new QCenterInfoDto(center.id, center.name, center.address))
                .from(center)
                .where(center.signed.eq(true)
                        ,(areaEq(sido, sigungu))
                        ,(centerNameEq(centerName)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<CenterPreview> findByPrefer(Long userId, Pageable pageable) {
        List<CenterPreview> content = jpaQueryFactory.select(new QCenterPreview(center, review.score.avg()))
                .from(center)
                .join(center.prefers, prefer).on(prefer.parent.id.eq(userId))
                .leftJoin(center.reviews, review)
                .groupBy(center)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

}
