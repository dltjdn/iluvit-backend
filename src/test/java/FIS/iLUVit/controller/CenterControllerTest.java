package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.CenterInfoDto;
import FIS.iLUVit.controller.dto.CenterInfoRequest;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.CenterBannerResponseDto;
import FIS.iLUVit.controller.dto.CenterSearchFilterDTO;
import FIS.iLUVit.controller.dto.CenterSearchMapFilterDTO;
import FIS.iLUVit.controller.messagecreate.ResponseRequests;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.dto.CenterPreview;
import FIS.iLUVit.service.CenterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static FIS.iLUVit.Creator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void 센터_검색_성공() throws Exception {

        //given
        List<Area> areas = Arrays.asList(new Area("sido", "sigungu"), new Area("sido2", "sigungu2"));
        CenterSearchFilterDTO request = centerSearchFilterRequest(areas);

        List<CenterPreview> response = centerPreviewResponse();
        SliceImpl<CenterPreview> slice = new SliceImpl<>(response, PageRequest.of(0, 2), false);

        // stubbing 참고로 doReturn 과 whenReturn 의 차이는 없다. 오히려 doReturn 컴파일 시점에 오류를 잡아줌
        Mockito.doReturn(slice).when(centerService)
                .findByFilter(anyList(), any(Theme.class), any(Integer.class), any(KindOf.class), any(Pageable.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/center/search?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        verify(centerService, times(1)).findByFilter(anyList(), any(Theme.class), any(Integer.class), any(KindOf.class), any(Pageable.class));
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(slice)));
    }

    @Test
    public void 센터_검색_지역3개이상요청시_실패() throws Exception {

        //given
        List<Area> areas = Arrays.asList(new Area("sido", "sigungu"), new Area("sido2", "sigungu2"), new Area("sido2", "sigungu2"), new Area("sido2", "sigungu2"));
        CenterSearchFilterDTO request = centerSearchFilterRequest(areas);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/center/search?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        verify(centerService, never()).findByFilter(anyList(), any(Theme.class), any(Integer.class), any(KindOf.class), any(Pageable.class));
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(HttpStatus.BAD_REQUEST, "[최소 1개 이상의 지역을 선택해야합니다]")
                )));
    }

    @Test
    public void 센터_검색_지역선택_없을시_실패() throws Exception {

        //given
        List<Area> areas = new ArrayList<>();
        CenterSearchFilterDTO request = centerSearchFilterRequest(areas);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/center/search?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then

        verify(centerService, never()).findByFilter(anyList(), any(Theme.class), any(Integer.class), any(KindOf.class), any(Pageable.class));
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(HttpStatus.BAD_REQUEST, "[최소 1개 이상의 지역을 선택해야합니다]")
                )));
    }

    @Test
    public void 지도에서_센터검색_성공() throws Exception {
        //given
        CenterSearchMapFilterDTO request = centerSearchMapFilterDTO(32.3213, 127.1231);
        CenterAndDistancePreview centerAndDistancePreview = centerAndDistancePreview();

        List<CenterAndDistancePreview> response = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            response.add(centerAndDistancePreview);
        }

        //stubbing
        Mockito.doReturn(response).when(centerService)
                .findByFilterAndMap(
                        request.getLongitude(),
                        request.getLatitude(),
                        request.getTheme(),
                        request.getInterestedAge(),
                        request.getKindOf(),
                        request.getDistance());

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/center/map/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        verify(centerService, times(1))
                .findByFilterAndMap(request.getLongitude(),
                        request.getLatitude(),
                        request.getTheme(),
                        request.getInterestedAge(),
                        request.getKindOf(),
                        request.getDistance());

        resultActions.andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

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

}










