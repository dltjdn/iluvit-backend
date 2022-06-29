package FIS.iLUVit.repository;

import FIS.iLUVit.config.AppConfig;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Kindergarten;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.repository.dto.CenterPreview;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class CenterRepositoryTest {

    @Autowired
    CenterRepository centerRepository;

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
    void findByThemeAndAgeOnly3() {
    }

    @Test
    void findCenterByTeacher() {
    }

    @Test
    void findByUser() {
    }

    @Test
    void findByIdAndSignedWithBoard() {
    }

    @Test
    void findByIdWithTeacher() {
    }
}