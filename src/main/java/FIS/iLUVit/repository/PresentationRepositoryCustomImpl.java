package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.PresentationPreviewForUsersResponse;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.PresentationPreviewForUsers;
import FIS.iLUVit.repository.dto.QPresentationPreviewForUsers;
import FIS.iLUVit.service.ImageService;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPresentation.presentation;
import static FIS.iLUVit.repository.PresentationQueryMethod.presentationSort;

@AllArgsConstructor
public class PresentationRepositoryCustomImpl extends CenterQueryMethod implements PresentationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final ImageService imageService;

    @Override
    public SliceImpl<PresentationPreviewForUsersResponse> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable) {

        LocalDate now = LocalDate.now();

        List<PresentationPreviewForUsers> content = jpaQueryFactory.select(new QPresentationPreviewForUsers(presentation, center))
                .from(presentation)
                .join(presentation.center, center)
                .where(areasIn(areas)
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge))
                        .and(kindOfEq(kindOf))
                        .and(presentation.endDate.goe(now))
                )
                .orderBy(Objects.requireNonNull(presentationSort(pageable)).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if(content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        List<PresentationPreviewForUsersResponse> collect = content.stream().map(c -> {
            PresentationPreviewForUsersResponse temp = new PresentationPreviewForUsersResponse(c);
            temp.setInfoImages(imageService.getInfoImages(c.getInfoImages()));
            return temp;
        }).collect(Collectors.toList());

        return new SliceImpl<>(collect, pageable, hasNext);

    }

}
