package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.center.CenterBannerResponse;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.center.CenterDetailRequest;
import FIS.iLUVit.controller.messagecreate.ResponseRequests;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.CenterErrorResult;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.CenterService;
import FIS.iLUVit.service.ChildService;
import FIS.iLUVit.service.TeacherService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static FIS.iLUVit.Creator.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class CenterControllerTest extends ResponseRequests {

    @InjectMocks
    CenterController target;
    @Mock
    CenterService centerService;
    @Mock
    TeacherService teacherService;

    @Mock
    ChildService childService;
    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    private void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), new LoginUserArgumentResolver("secretKey"))
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        objectMapper = new ObjectMapper();
    }

    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }



    @Test
    public void 회원가입과정에서center정보가져오기() throws Exception {
        // given
        String url = "/center/signup?page=0&size=5";
        CenterRequest request = CenterRequest.builder()
                .sido("서울시")
                .sigungu("금천구")
                .build();
        List<CenterDto> content = List.of(CenterDto.builder()
                .id(1L)
                .name("name")
                .address("address")
                .build());
        PageRequest pageable = PageRequest.of(0, 5);
        SliceImpl<CenterDto> response = new SliceImpl<>(content, pageable, false);
        doReturn(response)
                .when(teacherService)
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
            CenterBannerResponse response = new CenterBannerResponse(1L, "test", true, true, 4.5, null,"testLocation", List.of(new String[]{"dfd", "fsdfs"}));
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
            CenterBannerResponse response = new CenterBannerResponse(1L, "test", true, true, 4.5, null,"testLocation", List.of(new String[]{"dfd", "fsdfs"}));
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
            CenterBannerResponse response = new CenterBannerResponse(1L, "test", true, true, 4.5, null,"testLocation", List.of(new String[]{"dfd", "fsdfs"}));
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

        CenterDetailRequest wrongRequest;
        CenterDetailRequest rightRequest;
        MockMultipartFile multipartFile;
        List<MockMultipartFile> multipartFileList = new ArrayList<>();
        Center center;
        Teacher waitingTeacher;
        Teacher acceptTeacher;

        @BeforeEach
        void init() throws IOException {
            String name = "162693895955046828.png";
            Path path1 = Paths.get(new File("").getAbsolutePath() + '/' + name);
            byte[] content = Files.readAllBytes(path1);
            multipartFile = new MockMultipartFile(name, name, "image", content);
            multipartFileList.add(multipartFile);
            multipartFileList.add(multipartFile);

            waitingTeacher = createTeacher(1L, center, Auth.TEACHER, Approval.WAITING);
            acceptTeacher = createTeacher(1L, center, Auth.TEACHER, Approval.ACCEPT);
            wrongRequest = new CenterDetailRequest();
            rightRequest = CenterDetailRequest.builder()
                    .name("test")
                    .director("test 원장")
                    .tel("test 전번")
                    .address("주소")
                    .estType("공립")
//                    .sido("sido")
//                    .sigungu("sigungu")
                    .offerService("")
                    .recruit(false)
                    .theme(coding())
                    .build();
        }

        @Test
        @DisplayName("[error] 시설 요청 오류")
        public void 시설요청오류() throws Exception {
            //given

            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/center/1");
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod(HttpMethod.PATCH.toString());
                    return request;
                }
            });

            MockMultipartFile requestDto = new MockMultipartFile("requestDto", null,
                    "application/json", objectMapper.writeValueAsString(wrongRequest).getBytes());



            //when
            ResultActions result = mockMvc.perform(
                    builder.file("infoImages", multipartFile.getBytes())
                            .file("profileImage", multipartFile.getBytes())
                            .file(requestDto)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            );
            //then
            result.andDo(print())
                    .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("[error] 로그인 안하면 오류")
        public void 로그인X오류() throws Exception {
            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/center/1");
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod(HttpMethod.PATCH.toString());
                    return request;
                }
            });

            MockMultipartFile requestDto = new MockMultipartFile("requestDto", null,
                    "application/json", objectMapper.writeValueAsString(rightRequest).getBytes());



            //when
            ResultActions result = mockMvc.perform(
                    builder.file("infoImages", multipartFile.getBytes())
                            .file("profileImage", multipartFile.getBytes())
                            .file(requestDto)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            );

            //then
            result.andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.FORBIDDEN
                                    , "인증된 사용자가 아닙니다")
                    )));
        }

        @Test
        @DisplayName("[error] 로그인은 했으나 선생님이 아닌경우")
        public void 선생님이아닌경우() throws Exception {
            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/center/1");
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod(HttpMethod.PATCH.toString());
                    return request;
                }
            });

            Mockito.doThrow(new UserException(UserErrorResult.USER_NOT_EXIST))
                    .when(centerService).modifyCenterInfo(any(Long.class), any(Long.class), any(CenterDetailRequest.class));

            MockMultipartFile requestDto = new MockMultipartFile("requestDto", null,
                    "application/json", objectMapper.writeValueAsString(rightRequest).getBytes());



            //when
            ResultActions result = mockMvc.perform(
                    builder.file("infoImages", multipartFile.getBytes())
                            .file("profileImage", multipartFile.getBytes())
                            .file(requestDto)
                            .header(HttpHeaders.AUTHORIZATION, createJwtToken(createParent(1L)))
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            );

            //then
            result.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.BAD_REQUEST
                                    , UserErrorResult.USER_NOT_EXIST.getMessage())
                    )));
        }

        @Test
        @DisplayName("[error] 시설 수정 권한이 없는 경우")
        public void 시설작성권한없음() throws Exception {
            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/center/1");
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod(HttpMethod.PATCH.toString());
                    return request;
                }
            });

            Mockito.doThrow(new CenterException(CenterErrorResult.AUTHENTICATION_FAILED))
                    .when(centerService).modifyCenterInfo(any(Long.class), any(Long.class), any(CenterDetailRequest.class));

            MockMultipartFile requestDto = new MockMultipartFile("requestDto", null,
                    "application/json", objectMapper.writeValueAsString(rightRequest).getBytes());



            //when
            ResultActions result = mockMvc.perform(
                    builder.file("infoImages", multipartFile.getBytes())
                            .file("profileImage", multipartFile.getBytes())
                            .file(requestDto)
                            .header(HttpHeaders.AUTHORIZATION, createJwtToken(createParent(1L)))
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            );

            //then
            result.andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.FORBIDDEN
                                    , CenterErrorResult.AUTHENTICATION_FAILED.getMessage())
                    )));
        }

        @Test
        @DisplayName("[error] 시설 주소가 잘못된 경우")
        public void 시설주소가잘못된경우() throws Exception {
            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/center/1");
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod(HttpMethod.PATCH.toString());
                    return request;
                }
            });

            Mockito.doThrow(new CenterException(CenterErrorResult.CENTER_WRONG_ADDRESS))
                    .when(centerService).modifyCenterInfo(any(Long.class), any(Long.class), any(CenterDetailRequest.class));

            MockMultipartFile requestDto = new MockMultipartFile("requestDto", null,
                    "application/json", objectMapper.writeValueAsString(rightRequest).getBytes());



            //when
            ResultActions result = mockMvc.perform(
                    builder.file("infoImages", multipartFile.getBytes())
                            .file("profileImage", multipartFile.getBytes())
                            .file(requestDto)
                            .header(HttpHeaders.AUTHORIZATION, createJwtToken(createParent(1L)))
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            );

            //then
            result.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.BAD_REQUEST
                                    , CenterErrorResult.CENTER_WRONG_ADDRESS.getMessage())
                    )));
        }

        @Test
        @DisplayName("[success] 시설 수정 성공")
        public void 시설수정성공() throws Exception {
            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/center/1");
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod(HttpMethod.PATCH.toString());
                    return request;
                }
            });

            Mockito.doReturn(1L)
                    .when(centerService).modifyCenterInfo(any(Long.class), any(Long.class), any(CenterDetailRequest.class));

            MockMultipartFile requestDto = new MockMultipartFile("requestDto", null,
                    "application/json", objectMapper.writeValueAsString(rightRequest).getBytes());



            //when
            ResultActions result = mockMvc.perform(
                    builder.file("infoImages", multipartFile.getBytes())
                            .file("profileImage", multipartFile.getBytes())
                            .file(requestDto)
                            .header(HttpHeaders.AUTHORIZATION, createJwtToken(createParent(1L)))
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            );

            //then
            result.andDo(print())
                    .andExpect(status().isAccepted())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            1L
                    )));
        }

        @Test
        @DisplayName("[success] 시설 수정 성공")
        public void 시설이미지수정성공_APP() throws Exception {
            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/center/1/image");
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod(HttpMethod.PATCH.toString());
                    return request;
                }
            });

            Mockito.doReturn(1L)
                    .when(centerService).modifyCenterImage(any(Long.class), any(Long.class), anyList(), any(MultipartFile.class));

            MockMultipartFile requestDto = new MockMultipartFile("requestDto", null,
                    "application/json", objectMapper.writeValueAsString(rightRequest).getBytes());


            //when
            ResultActions result = mockMvc.perform(
                    builder.file("infoImages", multipartFile.getBytes())
                            .file("profileImage", multipartFile.getBytes())
                            .header(HttpHeaders.AUTHORIZATION, createJwtToken(createParent(1L)))
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            );

            //then
            result.andDo(print())
                    .andExpect(status().isAccepted())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            1L
                    )));
        }

        @Test
        @DisplayName("[success] 시설 수정 성공")
        public void 시설정보수정성공_APP() throws Exception {
            MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/center/1/info");
            builder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod(HttpMethod.PATCH.toString());
                    return request;
                }
            });

            Mockito.doReturn(1L)
                    .when(centerService).modifyCenterInfo(any(Long.class), any(Long.class), any(CenterDetailRequest.class));

            //when
            ResultActions result = mockMvc.perform(
                    builder
                            .content(objectMapper.writeValueAsString(rightRequest))
                            .header(HttpHeaders.AUTHORIZATION, createJwtToken(createParent(1L)))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            result.andDo(print())
                    .andExpect(status().isAccepted())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            1L
                    )));
        }
    }

    @Test
    public void 아이추가센터정보조회() throws Exception {
        // given
        String url = "/center/child/add?page=0&size=10";
        List<CenterDto> content = List.of(CenterDto.builder().build());
        CenterRequest request = CenterRequest.builder().build();
        Pageable pageable = PageRequest.of(0, 10);
        SliceImpl<CenterDto> response = new SliceImpl<>(content, pageable, false);
        doReturn(response)
                .when(childService)
                .findCenterForAddChild(any(), any());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(response)
                ));
    }

}










