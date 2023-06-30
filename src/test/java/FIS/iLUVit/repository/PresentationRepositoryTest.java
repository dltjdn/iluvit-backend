package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.dto.presentation.PresentationForUserResponse;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.dto.presentation.PresentationWithPtDatesDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static FIS.iLUVit.Creator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class PresentationRepositoryTest {

    @Nested
    @DisplayName("설명회_필터_검색")
    class FindByFilter{

        // TODO 설명회_검색_결과_없음

        // TODO 설명회_검색_결과_없음2
    }

    @Nested
    @DisplayName("시설 상세보기에서 설명회 버튼 눌렀을 때 조회 될 내용")
    class 설명회버튼조회내용{

        // TODO 학부모의 시설 설명회 상세보기 로그인 X

        // TODO 학부모 시dpdpeenpii
}
