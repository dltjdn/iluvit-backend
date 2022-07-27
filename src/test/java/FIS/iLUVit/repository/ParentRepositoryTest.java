package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import static FIS.iLUVit.Creator.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ParentRepositoryTest {

    @Nested
    @DisplayName("자신이 신청 - 취소 - 대기한 설명회 목록 가져오기 ")
    class 자신이신청취소대기한설명회목록{

        @Test
        @DisplayName("[success] 자신이 신청한 설명회 가져오기")
        public void 학부모가신청한설명회() throws Exception {
            //given
            Kindergarten center = createKindergarten("test");
            Parent parent = createParent();
            Presentation invalidPresentation = createInvalidPresentation(center);
            Presentation validPresentation = createValidPresentation(center);
            PtDate canRegisterPtDate = createCanRegisterPtDate(validPresentation);
            PtDate canNotRegisterPtDate = createCanNotRegisterPtDate(invalidPresentation);
            Waiting waiting1 = createWaiting(canRegisterPtDate, parent, 1);
//            createCancelParticipation();
//            createJoinParticipation();
            //when

            //then
        }
    }
}