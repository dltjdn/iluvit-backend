package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PresentationServiceTest {

    @Mock
    private PresentationRepository presentationRepository;
    @Mock
    private PtDateRepository ptDateRepository;
    @Mock
    private CenterRepository centerRepository;
    @Spy
    private ImageService imageService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WaitingRepository waitingRepository;
    @InjectMocks
    private PresentationService presentationService;

    @BeforeEach
    void init(){


        Presentation presentation = Presentation.builder()
                .id(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .place("테스트 장소")
                .content("테스트 설명회")
                .imgCnt(3)
                .videoCnt(1)
                .center(null)
                .build();

        Center kindergarten = Center.builder()
                .id(1L)
                .address("test 주소")
                .name("test 유치원")
                .build();

        PtDate ptDate1 = PtDate.builder()
                .id(1L)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .build();



    }

    @Test
    void 설명회_수정하기() {
        //given

        //when

        //then
    }

    @Test
    void 설명회_수정_잘못된_설명회_아이디로_접근시_오류(){
        //given

        //when

        //then
    }

    @Test
    void 설명회_수정_승인안된_교사가_수정시_오류(){
        //given

        //when

        //then
    }
}