package FIS.iLUVit.repository;

import FIS.iLUVit.domain.QPresentation;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;

import java.awt.print.Pageable;
import java.util.List;

import static FIS.iLUVit.domain.QPresentation.presentation;

@AllArgsConstructor
public class PresentationRepositoryCustomImpl implements PresentationRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable) {
        jpaQueryFactory.select(presentation)
                .from(presentation)
                .join(presentation.center).fetchJoin();

    }
}
