package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.controller.dto.PresentationPreviewForUsersResponse;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class PresentationRepositoryTest {

    @Autowired
    PresentationRepository target;

    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("설명회_필터_검색")
    class FindByFilter{

        @Test
        public void 설명회_검색_결과_없음() throws Exception {
            //given
            Theme theme = englishAndCoding();
            Area area1 = new Area("서울특별시", "금천구");
            Area area2 = new Area("서울특별시", "영등포구");
            List<Area> areas = new ArrayList<>();
            areas.add(area1);
            areas.add(area2);
            Integer minAge = 1;
            Integer maxAge = 3;

            Center center1 = createCenter("test", area1, theme, maxAge, minAge);
            Center center2 = createCenter("test", area1, theme, maxAge, minAge);
            Center center3 = createCenter("test", area1, theme, maxAge, minAge);
            Presentation presentation1 = createValidPresentation(center1, 1, 3);
            Presentation presentation2 = createValidPresentation(center2, 2, 3);
            Presentation presentation3 = createValidPresentation(center3, 1, 1);
            List<Presentation> presentations = new ArrayList<>();
            presentations.add(presentation3);
            presentations.add(presentation1);
            presentations.add(presentation2);
            em.persist(center1);
            em.persist(center2);
            em.persist(center3);
            em.persist(presentation1);
            em.persist(presentation2);
            em.persist(presentation3);
            em.flush();

            //when
            SliceImpl<PresentationPreviewForUsersResponse> results = target.findByFilter(areas, coding(), 3, KindOf.ALL, PageRequest.of(0, 4));

            //then
            assertThat(results.getContent().size())
                    .isEqualTo(3L);
            assertThat(results.hasNext())
                    .isFalse();
            int i = 0;
            for (PresentationPreviewForUsersResponse result : results) {
                assertThat(result.getEndDate()).isEqualTo(presentations.get(i).getEndDate());
                i++;
            }

        }
    }

    @Nested
    @DisplayName("시설 상세보기에서 설명회 버튼 눌렀을 때 조회 될 내용")
    class 설명회버튼조회내용{

        @Test
        @DisplayName("[success] 학부모의 시설 설명회 상세보기")
        public void 학부모의시설설명회상세보기() throws Exception {
            //given

            //when

            //then
        }
    }


}
