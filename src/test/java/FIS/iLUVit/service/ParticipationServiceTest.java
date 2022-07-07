package FIS.iLUVit.service;

import FIS.iLUVit.repository.ParticipationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.UserRepository;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ParticipationServiceTest {

    /**
     * 설명회 신청의 시나리오
     * 예외 사항
     * 1. 설명회 신청기간이 지났을 경우 예외 발생
     * 2. 설명회 신청가능인원이 가득 찼을 경우
     * 3. 설명회를 이미 신청했을 경우
     */

    @Mock
    UserRepository userRepository;
    @Mock
    ParticipationRepository participationRepository;
    @Mock
    PtDateRepository ptDateRepository;
    @InjectMocks
    ParticipationService participationService;


    @Test
    public void 설명회_신청_신청기간이지남_예외() throws Exception {
        //given
        /**
         * 설명회 신청을 위해서는 설명회 정보가 필요하다.
         * 컨트롤러에서 넘어온
         */

        //when

        //then

    }
}