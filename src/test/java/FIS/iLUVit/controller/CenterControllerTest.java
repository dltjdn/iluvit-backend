package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.CenterBannerResponseDto;
import FIS.iLUVit.controller.dto.CenterInfoDto;
import FIS.iLUVit.controller.dto.CenterInfoRequest;
import FIS.iLUVit.controller.messagecreate.ResponseRequests;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.CenterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static FIS.iLUVit.Creator.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class CenterControllerTest extends ResponseRequests {

    @InjectMocks
    CenterController centerController;
    @Mock
    CenterService centerService;
    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    private void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(centerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        objectMapper = new ObjectMapper();
    }



    @Test
    public void 회원가입과정에서center정보가져오기() throws Exception {
        // given
        String url = "/center/signup?page=0&size=5";
        CenterInfoRequest request = CenterInfoRequest.builder()
                .sido("서울시")
                .sigungu("금천구")
                .build();
        List<CenterInfoDto> content = List.of(CenterInfoDto.builder()
                .id(1L)
                .name("name")
                .address("address")
                .build());
        PageRequest pageable = PageRequest.of(0, 5);
        SliceImpl<CenterInfoDto> response = new SliceImpl<>(content, pageable, false);
        doReturn(response)
                .when(centerService)
                .findCenterForSignup(request, pageable);
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("sido", request.getSido())
                        .param("sigungu", request.getSigungu())
                        .param("centerName", request.getCenterName())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
    @Nested
    @DisplayName("센터_베너_정보_검색")
    class BannerControllerTest {

        @Test
        public void 센터_정보_검색_배너() throws Exception {
            //given
            CenterBannerResponseDto response = new CenterBannerResponseDto(1L, "test", true, true, 4.5, null,"testLocation", List.of(new String[]{"dfd", "fsdfs"}));
            Parent parent = createParent(1L);
            String jwtToken = createJwtToken(parent);

            doReturn(response)
                    .when(centerService).findBannerById(1L, 1L);

            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/center/1/recruit")
                            .header("Authorization", jwtToken)
            );

            //then
            verify(centerService, times(1))
                    .findBannerById(1L, 1L);

            result.andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(response)));

        }

        @Test
        public void 베너_검색_성공_선생님으로_검색() throws Exception {
            //given
            CenterBannerResponseDto response = new CenterBannerResponseDto(1L, "test", true, true, 4.5, null,"testLocation", List.of(new String[]{"dfd", "fsdfs"}));
            Teacher teacher = createTeacher(1L);
            String jwtToken = createJwtToken(teacher);

            doReturn(response)
                    .when(centerService).findBannerById(1L, 1L);

            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/center/1/recruit")
                            .header("Authorization", jwtToken)
            );

            //then
            verify(centerService, times(1))
                    .findBannerById(1L, 1L);

            result.andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(response)));
        }

        @Test
        public void 센터_베너_정보_검색_비회원() throws Exception {
            //given
            CenterBannerResponseDto response = new CenterBannerResponseDto(1L, "test", true, true, 4.5, null,"testLocation", List.of(new String[]{"dfd", "fsdfs"}));
//            Teacher teacher = createTeacher(1L);
//            String jwtToken = createJwtToken(teacher);

            doReturn(response)
                    .when(centerService).findBannerById(1L, null);

            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/center/1/recruit")
            );

            //then
            verify(centerService, times(1))
                    .findBannerById(1L, null);

            result.andDo(print())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(response)));
        }

    }

    @Nested
    @DisplayName("시설 수정")
    class 시설수정{
        @Test
        @DisplayName("[error] 시설 요청 오류")
        public void 시설요청오류() throws Exception {
            //given

            //when

            //then
        }
    }

}










