package FIS.iLUVit.repository;

import FIS.iLUVit.global.config.argumentResolver.ForDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class TeacherRepositoryTest {
    @Nested
    @DisplayName("findByIdAndNotAssign")
    class findByIdAndNotAssign{

        // TODO 해당 교사가 속해있는 시설이 있는 경우

        // TODO 해당 교사가 속해있는 시설이 없는 경우
    }

    // TODO find Director By Center

    // TODO find Director By Id

    // TODO find By Id With Center With Teacher

    @Nested
    @DisplayName("findDirectorByIdWithCenterWithTeacher")
    class findDirectorByIdWithCenterWithTeacher{

        // TODO 정상적인 요청

        // TODO 원장이 아닌 사용자의 요청
    }

    @Nested
    @DisplayName("findByIdWithCenterWithChildWithParent")
    class findByIdWithCenterWithChildWithParent{

        // TODO 승인받지 않은 교사의 요청

        // TODO 원장의 정상 요청

        // TODO 아이가 없는 경우
    }
}