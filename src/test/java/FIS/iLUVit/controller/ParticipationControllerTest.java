package FIS.iLUVit.controller;

import FIS.iLUVit.service.ParticipationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ParticipationControllerTest {

    @Mock
    ParticipationService participationService;
    @InjectMocks
    ParticipationController participationController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(participationService)
                .build();
    }

    @Test
    public void 설명회_신청_로그인안함() throws Exception {
        //given

        //when

        //then
    }
}