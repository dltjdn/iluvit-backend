package FIS.iLUVit.controller;

import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.CenterService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityManager;


@ExtendWith(MockitoExtension.class)
class CenterControllerTest {

//    @Autowired
//    CenterController centerController;
//    @MockBean
//    CenterService centerService;
//    @MockBean
//    UserRepository userRepository;
//    @MockBean
//    EntityManager entityManager;
//    @Autowired
//    MockMvc mockMvc;

    @InjectMocks
    CenterController centerController;

    @Mock
    CenterService centerService;

    MockMvc mockMvc;

    Gson gson;

    @BeforeEach
    private void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(centerController).build();
        gson = new Gson();
    }

    @Test
    void 센터_검색() throws Exception {
        System.out.println("Center Search Process start");

        //given

        //when

        // then

    }
}










