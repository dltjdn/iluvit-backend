package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.controller.dto.CenterInfoDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Kindergarten;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static FIS.iLUVit.Creator.createArea;
import static FIS.iLUVit.Creator.createKindergarten;
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
        List areas = new ArrayList<Area>();
        Area gumchon = createArea("서울특별시", "금천구");
        areas.add(gumchon);
        Theme theme = Theme.builder()
                .english(true)
                .art(true)
                .build();
        BasicInfra basicInfra = BasicInfra.builder()
                .hasCCTV(true)
                .cctvCnt(3)
                .build();
        Center center1 = createKindergarten(gumchon, "test1", theme, 2, 4, "test", "test", basicInfra, 5);
        Center center2 = createKindergarten(gumchon, "test2", theme, 3, 5, "test", "test", basicInfra, 3);
        Center center3 = createKindergarten(gumchon, "test3", theme, 2, 3, "test", "test", basicInfra, 1);

        em.persist(center1);
        em.persist(center2);
        em.persist(center3);
        em.flush();
        //when
        PageRequest pageable = PageRequest.of(0, 5);

        Slice<CenterPreview> byFilter = centerRepository.findByFilter(areas, theme, 3, KindOf.ALL, pageable);
        List<CenterPreview> contents = byFilter.getContent();

        //then
        assertThat(byFilter.hasNext()).isFalse();
        assertThat(byFilter.getSize()).isEqualTo(5);
        assertThat(byFilter.hasNext()).isFalse();
        assertThat(byFilter.getNumberOfElements()).isEqualTo(3);
         assertThat(contents.get(0).getName()).isEqualTo("test1");
        assertThat(contents.get(1).getName()).isEqualTo("test2");
        assertThat(contents.get(2).getName()).isEqualTo("test3");
    }

    @Test
    public void 시설_프리뷰_정보_조회_테마가_전부_null_일때() throws Exception {
        //given
        List areas = new ArrayList<Area>();
        Area gumchon = createArea("서울특별시", "금천구");
        areas.add(gumchon);
        Theme theme = Theme.builder()
                .english(null)
                .art(null)
                .build();
        BasicInfra basicInfra = BasicInfra.builder()
                .hasCCTV(true)
                .cctvCnt(3)
                .build();
        Center center1 = createKindergarten(gumchon, "test1", theme, 2, 4, "test", "test", basicInfra, 5);
        Center center2 = createKindergarten(gumchon, "test2", theme, 3, 5, "test", "test", basicInfra, 3);
        Center center3 = createKindergarten(gumchon, "test3", theme, 2, 3, "test", "test", basicInfra, 1);

        em.persist(center1);
        em.persist(center2);
        em.persist(center3);
        em.flush();
        //when
        PageRequest pageable = PageRequest.of(0, 5);

        Slice<CenterPreview> byFilter = centerRepository.findByFilter(areas, theme, 3, KindOf.ALL, pageable);
        List<CenterPreview> contents = byFilter.getContent();

        //then
        assertThat(byFilter.hasNext()).isFalse();
        assertThat(byFilter.getSize()).isEqualTo(5);
        assertThat(byFilter.hasNext()).isFalse();
        assertThat(byFilter.getNumberOfElements()).isEqualTo(3);
        assertThat(contents.get(0).getName()).isEqualTo("test1");
        assertThat(contents.get(1).getName()).isEqualTo("test2");
        assertThat(contents.get(2).getName()).isEqualTo("test3");
    }

    @Test
    public void findByIdWithTeacher_해당시설에선생이등록된경우() {
        // given
        Kindergarten center1 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, null, null);
        Kindergarten center2 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, null, null);
        Teacher teacher1 = Teacher.builder()
                .name("teacher1")
                .center(center1)
                .build();
        Teacher teacher2 = Teacher.builder()
                .name("teacher2")
                .center(center1)
                .build();
        Teacher teacher3 = Teacher.builder()
                .name("teacher3")
                .center(center2)
                .build();
        em.persist(center1);
        em.persist(center2);
        em.persist(teacher1);
        em.persist(teacher2);
        em.persist(teacher3);
        em.flush();
        em.clear();
        // when
        Center result = centerRepository.findByIdWithTeacher(center1.getId()).orElse(null);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(center1.getId());
        assertThat(result.getTeachers().size()).isEqualTo(2);
    }

    @Test
    public void findByIdWithTeacher_해당시설에선생이없는경우() {
        // given
        Kindergarten center1 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, null, null);
        Kindergarten center2 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, null, null);
        Teacher teacher1 = Teacher.builder()
                .name("teacher1")
                .center(center2)
                .build();
        Teacher teacher2 = Teacher.builder()
                .name("teacher2")
                .center(center2)
                .build();
        Teacher teacher3 = Teacher.builder()
                .name("teacher3")
                .center(center2)
                .build();
        em.persist(center1);
        em.persist(center2);
        em.persist(teacher1);
        em.persist(teacher2);
        em.persist(teacher3);
        em.flush();
        em.clear();
        // when
        Center result = centerRepository.findByIdWithTeacher(center1.getId()).orElse(null);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(center1.getId());
        assertThat(result.getTeachers().size()).isEqualTo(0);
    }
    
    @Test
    public void 회원가입을위한시설정보조회() {
        // given
        Kindergarten center1 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), true,
                false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, null, null);
        Kindergarten center2 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, null, null);
        em.persist(center1);
        em.persist(center2);
        em.flush();
        em.clear();
        // when
        Slice<CenterInfoDto> result = centerRepository.findForSignup("서울시", "금천구", "떡잎유치원", PageRequest.of(0, 5));
        // then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo(center1.getName());
        assertThat(result.getContent().get(0).getAddress()).isEqualTo(center1.getAddress());
    } 
}