package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.dto.presentation.PresentationForUserResponse;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPresentation.presentation;
import static FIS.iLUVit.repository.PresentationQueryMethod.presentationSort;

@AllArgsConstructor
public class PresentationRepositoryCustomImpl extends CenterQueryMethod implements PresentationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
        지역, 테마, 관심연령, 시설종류, 설명회 종료일, 검색어에 해당하는 설명회 리스트를 조회합니다
     */
    @Override
    public Slice<Presentation> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, String searchContent, Pageable pageable) {

        List<Presentation> presentations = jpaQueryFactory.select(presentation)
                .from(presentation)
                .join(presentation.center, center)
                .where(areasIn(areas)
                        ,themeEq(theme)
                        ,interestedAgeEq(interestedAge)
                        ,kindOfEq(kindOf)
                        ,presentation.endDate.goe(LocalDate.now())
                        ,centerNameEq(searchContent)
                )
                .orderBy(Objects.requireNonNull(presentationSort(pageable)).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        boolean hasNext = false;
        if(presentations.size() > pageable.getPageSize()){
            presentations.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(presentations, pageable, hasNext);

    }

}
