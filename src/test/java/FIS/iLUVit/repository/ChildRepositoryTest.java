package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.Approval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class ChildRepositoryTest {
    @Nested
    @DisplayName("findByIdAndParentWithCenter")
    class findByWithParentAndCenter {

        // TODO 정상 요청

        // TODO 잘못된 요청
    }

    // TODO find By User With Center

    @Nested
    @DisplayName("findByIdAndParent")
    class findByAndParent {

        // TODO 아이가 시설에 속한 경우

        // TODO 아이가 시설에 속하지 않은 경우
    }
}