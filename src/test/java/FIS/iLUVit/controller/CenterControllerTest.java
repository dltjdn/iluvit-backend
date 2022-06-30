package FIS.iLUVit.controller;

import FIS.iLUVit.service.CenterService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CenterControllerTest {

    // Mock 객체 주입
    @InjectMocks
    CenterController centerController;

    // 행위, 단위 테스트를 위한 Mock 객체 작성
    @Mock
    CenterService centerService;

    // 일반적인 방법으로는 HTTP 호출이 불가능하므로 스프링에서 이를 위한 MockMVC 제공한다.
    private MockMvc mockMvc;



}