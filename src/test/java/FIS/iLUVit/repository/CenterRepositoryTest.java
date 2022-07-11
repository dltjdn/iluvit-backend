package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Kindergarten;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class CenterRepositoryTest {

    @Autowired
    CenterRepository centerRepository;

    @Autowired
    EntityManager em;

    @Test
    void findBannerById() {
        Kindergarten center1 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, null, null);
        Kindergarten saved = centerRepository.save(center1);
        assertThat(saved.getName()).isEqualTo("떡잎유치원");

        List<Area> areas = new ArrayList<>();
        areas.add(new Area("서울시", "금천구"));
        Slice<CenterPreview> byFilter = centerRepository.findByFilter(areas, null, null, null, PageRequest.of(0, 10));
        CenterPreview centerPreview = byFilter.getContent().get(0);
        assertThat(centerPreview.getName()).isEqualTo("떡잎유치원");

    }

    @Test
    public void 시설_프리뷰_정보_조회() throws Exception {
        //given
        Area gumchon = createArea("서울특별시", "금천구");
        Theme theme = Theme.builder()
                .english(true)
                .art(true)
                .build();
        BasicInfra basicInfra = BasicInfra.builder()
                .hasCCTV(true)
                .cctvCnt(3)
                .build();
        Center center1 = createKindergarten(gumchon, "test1", theme, 2, 4, "test", "test", basicInfra);
        Center center2 = createKindergarten(gumchon, "test2", theme, 4, 5, "test", "test", basicInfra);
        Center center3 = createKindergarten(gumchon, "test3", theme, 2, 3, "test", "test", basicInfra);

        em.persist(center1);
        em.persist(center2);
        em.persist(center3);
        em.flush();
        //when

        //centerRepository.findByFilter(gumchon, theme, 4, "ALL",)

        //then
    }
}