package FIS.iLUVit.repository;

import FIS.iLUVit.dto.presentation.PresentationForUserResponse;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.dto.presentation.PresentationForUserDto;
import FIS.iLUVit.dto.presentation.QPresentationForUserDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static FIS.iLUVit.domain.QCenter.center;
import static FIS.iLUVit.domain.QPresentation.presentation;
import static FIS.iLUVit.repository.PresentationQueryMethod.presentationSort;

@AllArgsConstructor
public class PresentationRepositoryCustomImpl extends CenterQueryMethod implements PresentationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public SliceImpl<PresentationForUserResponse> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, String searchContent, Pageable pageable) {

        LocalDate now = LocalDate.now();

        List<PresentationForUserDto> presentations = jpaQueryFactory.select(new QPresentationForUserDto(presentation, center))
                .from(presentation)
                .join(presentation.center, center)
                .where(areasIn(areas)
                        .and(themeEq(theme))
                        .and(interestedAgeEq(interestedAge))
                        .and(kindOfEq(kindOf))
                        .and(presentation.endDate.goe(now))
                        .and(centerNameEq(searchContent))
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

        List<PresentationForUserResponse> collect = presentations.stream().map(presentation -> {
            String infoImagePath = presentation.getInfoImages();
            List<String> infoImages;
            if(infoImagePath == null || infoImagePath.equals(""))
                infoImages = new ArrayList<>();
            else
                infoImages = List.of(infoImagePath.split(","));
            PresentationForUserResponse presentationResponse = new PresentationForUserResponse(presentation, infoImages);
            return presentationResponse;
        }).collect(Collectors.toList());

        return new SliceImpl<>(collect, pageable, hasNext);

    }

}
