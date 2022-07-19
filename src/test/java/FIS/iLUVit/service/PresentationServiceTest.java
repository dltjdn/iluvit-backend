package FIS.iLUVit.service;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;

class PresentationServiceTest {

    @Mock
    private PresentationRepository presentationRepository;
    @Mock
    private PtDateRepository ptDateRepository;
    @Mock
    private CenterRepository centerRepository;
    @Spy
    private LocalImageService imageService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WaitingRepository waitingRepository;
    @InjectMocks
    private PresentationService presentationService;

    Center center;
    Center center2;
    Presentation presentation;
    PtDate ptDate1;
    PtDate ptDate2;
    MockMultipartFile mockMultipartFile;

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fileInputStream);
    }

    @BeforeEach
    void init() throws IOException {

        //String fileName = "testCustomerUpload";
        //String contentType = "xls";
        //String filePath = "src/test/resources/excel/testCustomerUpload.xls";
        //mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        center = Center.builder()
                .id(1L)
                .name("test 유치원")
                .build();

        presentation = Presentation.builder()
                .id(1L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .place("테스트 장소")
                .content("테스트 설명회")
                .imgCnt(3)
                .videoCnt(1)
                .center(center)
                .build();

        center2 = Center.builder()
                .id(1L)
                .address("test 주소")
                .name("test 유치원")
                .build();

        ptDate1 = PtDate.builder()
                .id(1L)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .build();

        ptDate2 = PtDate.builder()
                .id(2L)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .presentation(presentation)
                .build();

        Parent parent = Parent.builder()
                .auth(Auth.PARENT)
                .name("test")
                .build();

        Participation participation = Participation.builder()
                .ptDate(ptDate1)
                .parent(parent)
                .status(Status.JOINED)
                .build();

    }

    @Test
    void 설명회_수정하기() {
        //given
//        String centerDir = imageService.getCenterDir(1L);
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