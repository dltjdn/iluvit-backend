package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.controller.dto.CenterInfoDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import FIS.iLUVit.repository.dto.CenterMapPreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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

    @Nested
    @DisplayName("지도 기반 센터 검색")
    class 지도기반센터검색{
        @Test
        @DisplayName("[success] 지도 리스트에 나올 정보")
        public void 지도기반검색하기() throws Exception {
            //given
            Parent parent1 = createParent();
            Parent parent2 = createParent();

            Center center1 = createCenter("이승범 어린이집", 3, 37.3912106, 127.0150178);
            Center center2 = createCenter("현승구 어린이집", 3, 37.5686264, 127.0113184);
            Center center3 = createCenter("이창윤 어린이집", 3, 37.5675523, 127.0147458);
            Center center4 = createCenter("김유정 어린이집", 3, 37.5500494, 127.0097435);
            Center center5 = createCenter("신은수 어린이집", 3, 37.5618861, 127.020072);
            Center center6 = createCenter("한명수 어린이집", 3, 37.5105178, 127.0147458);

            Prefer prefer1 = createPrefer(parent1, center1);
            Prefer prefer2 = createPrefer(parent1, center2);
            Prefer prefer3 = createPrefer(parent1, center3);
            Prefer prefer4 = createPrefer(parent1, center4);
            Prefer prefer5 = createPrefer(parent2, center1);
            Prefer prefer6 = createPrefer(parent2, center3);
            Prefer prefer7 = createPrefer(parent2, center4);
            Prefer prefer8 = createPrefer(parent2, center5);


            em.persist(parent1);
            em.persist(parent2);
            em.persist(center1);
            em.persist(center2);
            em.persist(center3);
            em.persist(center4);
            em.persist(center5);
            em.persist(center6);
            em.persist(prefer1);
            em.persist(prefer2);
            em.persist(prefer3);
            em.persist(prefer4);
            em.persist(prefer5);
            em.persist(prefer6);
            em.persist(prefer7);
            em.persist(prefer8);
            em.flush();
            em.clear();
            List<Long> idList = new ArrayList<>();
            idList.add(center1.getId());
            idList.add(center2.getId());
            idList.add(center3.getId());
            idList.add(center4.getId());


            //when
            SliceImpl<CenterAndDistancePreview> result = centerRepository.findByFilterForMapList(127.0147458, 37.5015178, KindOf.ALL, idList, PageRequest.of(0, 10));
            //then
            assertThat(result.getContent().size()).isEqualTo(4);
            // 로그인 안 했으므로 항상 false
            assertThat((int) result.stream().filter(CenterAndDistancePreview::getPrefer).count()).isEqualTo(0);
            result.forEach(centerAndDistancePreview -> System.out.println("centerAndDistancePreview.getDistance() = " + centerAndDistancePreview.getDistance()));
        }

        @Test
        @DisplayName("[success] 지도 리스트에 나올 정보")
        public void 지도기반검색하기2() throws Exception {
            //given
            Parent parent1 = createParent();
            Parent parent2 = createParent();

            Center center1 = createCenter("이승범 어린이집", 3, 37.3912106, 127.0150178);
            Center center2 = createCenter("현승구 어린이집", 3, 37.5686264, 127.0113184);
            Center center3 = createCenter("이창윤 어린이집", 3, 37.5675523, 127.0147458);
            Center center4 = createCenter("김유정 어린이집", 3, 37.5500494, 127.0097435);
            Center center5 = createCenter("신은수 어린이집", 3, 37.5618861, 127.020072);
            Center center6 = createCenter("한명수 어린이집", 3, 37.5105178, 127.0147458);

            Prefer prefer1 = createPrefer(parent1, center1);
            Prefer prefer2 = createPrefer(parent1, center2);
            Prefer prefer3 = createPrefer(parent1, center3);
            Prefer prefer4 = createPrefer(parent1, center4);
            Prefer prefer5 = createPrefer(parent2, center1);
            Prefer prefer6 = createPrefer(parent2, center3);
            Prefer prefer7 = createPrefer(parent2, center4);
            Prefer prefer8 = createPrefer(parent2, center5);

            em.persist(parent1);
            em.persist(parent2);
            em.persist(center1);
            em.persist(center2);
            em.persist(center3);
            em.persist(center4);
            em.persist(center5);
            em.persist(center6);
            em.persist(prefer1);
            em.persist(prefer2);
            em.persist(prefer3);
            em.persist(prefer4);
            em.persist(prefer5);
            em.persist(prefer6);
            em.persist(prefer7);
            em.persist(prefer8);
            em.flush();
            em.clear();

            List<Long> idList = new ArrayList<>();
            idList.add(center1.getId());
            idList.add(center2.getId());
            idList.add(center3.getId());

            //when
            SliceImpl<CenterAndDistancePreview> result = centerRepository.findByFilterForMapList(127.0147458, 37.5015178, parent1.getId(), KindOf.ALL, idList, PageRequest.of(0, 2));
            //then
            assertThat(result.getContent().size()).isEqualTo(2);
            assertThat(result.hasNext()).isTrue();
            assertThat((int) result.stream().filter(CenterAndDistancePreview::getPrefer).count()).isEqualTo(2);
            result.forEach(centerAndDistancePreview -> System.out.println("centerAndDistancePreview.getDistance() = " + centerAndDistancePreview.getDistance()));
        }

        @Test
        @DisplayName("[success] 지도 리스트에 나올 정보 어린이집/유치원으로 분리")
        public void 지도기반검색하기3() throws Exception {
            //given
            Parent parent1 = createParent();
            Parent parent2 = createParent();

            Center center1 = createKindergarten("이승범 어린이집", 3, 37.3912106, 127.0150178);
            Center center2 = createChildHouse("현승구 어린이집", 3, 37.5686264, 127.0113184);
            Center center3 = createKindergarten("이창윤 어린이집", 3, 37.5675523, 127.0147458);
            Center center4 = createChildHouse("김유정 어린이집", 3, 37.5500494, 127.0097435);
            Center center5 = createKindergarten("신은수 어린이집", 3, 37.5618861, 127.020072);
            Center center6 = createKindergarten("한명수 어린이집", 3, 37.5105178, 127.0147458);

            Prefer prefer1 = createPrefer(parent1, center1);
            Prefer prefer2 = createPrefer(parent1, center2);
            Prefer prefer3 = createPrefer(parent1, center3);
            Prefer prefer4 = createPrefer(parent1, center4);
            Prefer prefer5 = createPrefer(parent2, center1);
            Prefer prefer6 = createPrefer(parent2, center3);
            Prefer prefer7 = createPrefer(parent2, center4);
            Prefer prefer8 = createPrefer(parent2, center5);

            em.persist(parent1);
            em.persist(parent2);
            em.persist(center1);
            em.persist(center2);
            em.persist(center3);
            em.persist(center4);
            em.persist(center5);
            em.persist(center6);
            em.persist(prefer1);
            em.persist(prefer2);
            em.persist(prefer3);
            em.persist(prefer4);
            em.persist(prefer5);
            em.persist(prefer6);
            em.persist(prefer7);
            em.persist(prefer8);
            em.flush();
            em.clear();
            List<Long> idList = new ArrayList<>();
            idList.add(center1.getId());
            idList.add(center2.getId());
            idList.add(center3.getId());
            idList.add(center4.getId());
            idList.add(center5.getId());

            //when
            SliceImpl<CenterAndDistancePreview> result = centerRepository.findByFilterForMapList(127.0147458, 37.5015178,  parent1.getId(), KindOf.Kindergarten, idList, PageRequest.of(0, 5));
            //then
            assertThat(result.getContent().size()).isEqualTo(3);
            assertThat(result.hasNext()).isFalse();
            assertThat((int) result.stream().filter(CenterAndDistancePreview::getPrefer).count()).isEqualTo(2);
            result.forEach(centerAndDistancePreview -> System.out.println("centerAndDistancePreview.getDistance() = " + centerAndDistancePreview.getDistance()));
        }

        @Test
        @DisplayName("[success] 지도에 뿌려줄 센터 정보")
        public void 지도에뿌려줄센터정보() throws Exception {
            //given
            Center center1 = createCenter("이승범 어린이집", 3, 37.3912106, 127.0150178);
            Center center2 = createCenter("현승구 어린이집", 3, 37.5686264, 127.0113184);
            Center center3 = createCenter("이창윤 어린이집", 3, 37.5675523, 127.0147458);
            Center center4 = createCenter("김유정 어린이집", 3, 37.5500494, 127.0097435);
            Center center5 = createCenter("신은수 어린이집", 3, 37.5618861, 127.020072);
            Center center6 = createCenter("한명수 어린이집", 3, 37.5105178, 127.0147458);
            em.persist(center1);
            em.persist(center2);
            em.persist(center3);
            em.persist(center4);
            em.persist(center5);
            em.persist(center6);
            em.flush();
            em.clear();
            //when
            List<CenterMapPreview> result = centerRepository.findByFilterForMap(127.0147458, 37.5015178, 100);

            //then
            assertThat(result.size()).isEqualTo(6);
        }
    }

    @Nested
    @DisplayName("센터_베너찾기")
    public class Banner{

        @Test
        public void 특정_시설의_베너정보_찾아오기_로그인_X() throws Exception {
            //given
            Theme theme = englishAndCoding();
            Center center = createCenter("test", true, true, theme);
            Review review1 = createReview(center, 5);
            Review review2 = createReview(center, 4);
            Review review3 = createReview(center, 3);

            em.persist(center);
            em.persist(review1);
            em.persist(review2);
            em.persist(review3);
            em.flush();
            //when
            CenterBannerDto result = centerRepository.findBannerById(center.getId());

            //then
            assertThat(result.getCenterId()).isEqualTo(center.getId());
            assertThat(result.getName()).isEqualTo("test");
            assertThat(result.getPrefer()).isNotNull().isFalse();
            assertThat(result.getStarAverage()).isEqualTo(4.0);

        }

        @Test
        public void 특정_시설의_베너정보_찾아오기_로그인_O_시설_북마크_했음() throws Exception {
            //given
            Theme theme = englishAndCoding();
            Parent parent = Creator.createParent();
            Center center = createCenter("test", true, true, theme);
            Prefer prefer = createPrefer(parent, center);
            Review review1 = createReview(center, 5);
            Review review2 = createReview( center, 4);
            Review review3 = createReview(center, 3);
            em.persist(center);
            em.persist(parent);
            em.persist(prefer);
            em.persist(review1);
            em.persist(review2);
            em.persist(review3);
            em.flush();
            //when
            CenterBannerDto result = centerRepository.findBannerById(center.getId(), parent.getId());

            //then
            assertThat(result.getCenterId()).isEqualTo(center.getId());
            assertThat(result.getName()).isEqualTo("test");
            assertThat(result.getPrefer()).isNotNull().isTrue();
            assertThat(result.getStarAverage()).isEqualTo(4.0);

        }

        @Test
        public void 특정_시설의_베너정보_찾아오기_로그인_O_시설_북마크_안했음() throws Exception {
            //given
            Theme theme = englishAndCoding();
            Parent parent = Creator.createParent();
            Center center = createCenter("test", true, true, theme);
            Review review1 = createReview(center, 5);
            Review review2 = createReview(center, 4);
            Review review3 = createReview(center, 3);
            em.persist(parent);
            em.persist(center);
            em.persist(review1);
            em.persist(review2);
            em.persist(review3);
            em.flush();
            //when
            CenterBannerDto result = centerRepository.findBannerById(center.getId(), parent.getId());

            //then
            assertThat(result.getCenterId()).isEqualTo(center.getId());
            assertThat(result.getName()).isEqualTo("test");
            assertThat(result.getPrefer()).isNotNull().isFalse();
            assertThat(result.getStarAverage()).isEqualTo(4.0);

        }

        @Test
        public void 특정_시설의_베너정보_찾아오기_로그인_O_선생으로_검색() throws Exception {
            //given
            Theme theme = englishAndCoding();
            Teacher teacher = Creator.createTeacher();
            Center center = createCenter("test", true, true, theme);
            Review review1 = createReview(center, 5);
            Review review2 = createReview(center, 4);
            Review review3 = createReview(center, 3);
            em.persist(teacher);
            em.persist(center);
            em.persist(review1);
            em.persist(review2);
            em.persist(review3);
            em.flush();
            //when
            CenterBannerDto result = centerRepository.findBannerById(center.getId(), teacher.getId());

            //then
            assertThat(result.getCenterId()).isEqualTo(center.getId());
            assertThat(result.getName()).isEqualTo("test");
            assertThat(result.getPrefer()).isNotNull().isFalse();
            assertThat(result.getStarAverage()).isEqualTo(4.0);

        }

        @Test
        public void 잘못된_시설_아이디_배너정보_없음() throws Exception {
            Theme theme = englishAndCoding();
            Parent parent = Creator.createParent();
            Center center = createCenter("test", true, true, theme);
            Review review1 = createReview(center, 5);
            Review review2 = createReview(center, 4);
            Review review3 = createReview(center, 3);
            em.persist(center);
            em.persist(review1);
            em.persist(review2);
            em.persist(review3);
            em.flush();
            //when
            CenterBannerDto result = centerRepository.findBannerById(1000L, parent.getId());
            CenterBannerDto result2 = centerRepository.findBannerById(1000L);
            CenterBannerDto result3 = centerRepository.findBannerById(center.getId(), parent.getId());
            //then
            assertThat(result).isNull();
            assertThat(result2).isNull();
            assertThat(result3).isNotNull();
        }
    }
}