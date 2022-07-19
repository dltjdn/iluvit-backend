package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    CenterRepository target;


    @Autowired
    EntityManager em;

    @Test
    void findBannerById() {
        Kindergarten center1 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, null, null);
        Kindergarten saved = target.save(center1);
        assertThat(saved.getName()).isEqualTo("떡잎유치원");

        List<Area> areas = new ArrayList<>();
        areas.add(new Area("서울시", "금천구"));
        Slice<CenterPreview> byFilter = target.findByFilter(areas, null, null, null, PageRequest.of(0, 10));
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

        Slice<CenterPreview> byFilter = target.findByFilter(areas, theme, 3, KindOf.ALL, pageable);
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

        Slice<CenterPreview> byFilter = target.findByFilter(areas, theme, 3, KindOf.ALL, pageable);
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

    @Nested
    @DisplayName("맵_기반_검색")
    public class CenterMapTets{

        @Test
        public void 현재_위치_중심_센터_찾기() throws Exception {
            //given
            // 현재위치
            double longitude;
            double latitude;
            Theme theme;
            Integer interestAge;

            //when
            // 대략적인 거리로만 반환 거리 계산은 service 에서? ㄴㄴ querydsl 로 해서 할 것
//            centerRepository.findByMapFilter()
            //then
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
            CenterBannerDto result = target.findBannerById(center.getId());

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
            CenterBannerDto result = target.findBannerById(center.getId(), parent.getId());

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
            CenterBannerDto result = target.findBannerById(center.getId(), parent.getId());

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
            CenterBannerDto result = target.findBannerById(center.getId(), teacher.getId());

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
            CenterBannerDto result = target.findBannerById(1000L, parent.getId());
            CenterBannerDto result2 = target.findBannerById(1000L);
            CenterBannerDto result3 = target.findBannerById(center.getId(), parent.getId());
            //then
            assertThat(result).isNull();
            assertThat(result2).isNull();
            assertThat(result3).isNotNull();
        }
    }
}