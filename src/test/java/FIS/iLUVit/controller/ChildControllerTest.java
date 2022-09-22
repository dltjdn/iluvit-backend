package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.ChildInfoDTO;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ChildService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChildControllerTest {

    @InjectMocks
    private ChildController target;

    @Mock
    private ChildService childService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private MockMultipartFile multipartFile;

    private Parent parent;
    private Child child;
    private Center center;
    private Teacher teacher;

    @BeforeEach
    public void init() throws IOException {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), new LoginUserArgumentResolver("secretKey"))
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile("profileImg", name, "image", content);
        parent = Creator.createParent(1L);
        center = Creator.createCenter(3L, "center");
        child = Creator.createChild(2L, "child", parent, center, Approval.ACCEPT);
        teacher = Creator.createTeacher(4L, "teacher", center, Auth.TEACHER, Approval.ACCEPT);
    }

    @Test
    public void 아이들정보반환() throws Exception {
        // given
        String url = "/parent/childInfo";
        doReturn(new ChildInfoDTO())
                .when(childService)
                .childrenInfo(any());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", Creator.createJwtToken(parent))
        );
        // then
        result.andExpect(status().isOk());
    }

    @Nested
    @DisplayName("아이승인")
    class acceptChild {

        @Test
        @DisplayName("[error] 승인받지않은교사요청")
        public void 승인받지않은교사요청() throws Exception {
            // given
            String url = "/teacher/child/accept/{childId}";
            UserErrorResult error = UserErrorResult.HAVE_NOT_AUTHORIZATION;
            doThrow(new UserException(error))
                    .when(childService)
                    .acceptChild(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isForbidden())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 아이 아이디 에러")
        public void 아이아이디에러() throws Exception {
            // given
            String url = "/teacher/child/accept/{childId}";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(childService)
                    .acceptChild(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 아이승인성공")
        public void 아이승인성공() throws Exception {
            // given
            String url = "/teacher/child/accept/{childId}";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(teacher))

            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("아이삭제")
    class fireChild {

        @Test
        @DisplayName("[error] 승인받지 않은 교사")
        public void 승인받지않은교사() throws Exception {
            // given
            String url = "/teacher/child/fire/{childId}";
            UserErrorResult error = UserErrorResult.HAVE_NOT_AUTHORIZATION;
            doThrow(new UserException(error))
                    .when(childService)
                    .fireChild(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, child.getId())
                            .header("Authorization", teacher.getId())
            );
            // then
            result.andExpect(status().isForbidden())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 아이 아이디 에러")
        public void 아이아이디에러() throws Exception {
            // given
            String url = "/teacher/child/fire/{childId}";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(childService)
                    .fireChild(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, child.getId())
                            .header("Authorization", teacher.getId())
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 아이삭제성공")
        public void 아이삭제성공() throws Exception {
            // given
            String url = "/teacher/child/fire/{childId}";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, child.getId())
                            .header("Authorization", teacher.getId())
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("아이 프로필 수정")
    class updateChild {

        @Test
        @DisplayName("[error] 식별자값 에러")
        public void 식별자에러() throws Exception {
            // given
            MockMultipartHttpServletRequestBuilder builder =
                    MockMvcRequestBuilders.multipart("/parent/child/{childId}?page=0&size=10", child.getId());
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(childService)
                    .updateChild(any(), any(), any());
            // when
            ResultActions result = mockMvc.perform(builder
                    .file(multipartFile)
                    .header("Authorization", Creator.createJwtToken(parent))
                    .param("center_id", "1")
                    .param("name", "name")
                    .param("birthDate", "2022-01-01"));
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 요청 validation error")
        public void validationError() throws Exception {
            // given
            MockMultipartHttpServletRequestBuilder builder =
                    MockMvcRequestBuilders.multipart("/parent/child/{childId}?page=0&size=10", child.getId());
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });
            // when
            ResultActions result = mockMvc.perform(builder
                    .file(multipartFile)
                    .header("Authorization", Creator.createJwtToken(parent))
                    .param("center_id", "1")
                    .param("name", "name")
                    .param("birthDate", "missMatch"));
            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("[success] 수정성공")
        public void 수정성공() throws Exception {
            // given
            MockMultipartHttpServletRequestBuilder builder =
                    MockMvcRequestBuilders.multipart("/parent/child/{childId}?page=0&size=10", child.getId());
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });
            // when
            ResultActions result = mockMvc.perform(builder
                    .file(multipartFile)
                    .header("Authorization", Creator.createJwtToken(parent))
                    .param("center_id", "1")
                    .param("name", "name")
                    .param("birthDate", "2022-01-01"));
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("학부모/아이 시설 승인 요청")
    class mappingCenter {
        @Test
        public void 잘못된아이정보() throws Exception {
            //given
            String url = "/parent/child/center/{childId}/{centerId}";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(childService)
                    .mappingCenter(any(), any(), any());
            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, child.getId(), center.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            //then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        public void 아이가시설에속해있는경우() throws Exception {
            //given
            String url = "/parent/child/center/{childId}/{centerId}";
            SignupErrorResult error = SignupErrorResult.ALREADY_BELONG_CENTER;
            doThrow(new SignupException(error))
                    .when(childService)
                    .mappingCenter(any(), any(), any());
            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, parent.getId(), child.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            //then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        public void 잘못된시설정보() throws Exception {
            //given
            String url = "/parent/child/center/{childId}/{centerId}";
            CenterErrorResult error = CenterErrorResult.CENTER_NOT_EXIST;
            doThrow(new CenterException(error))
                    .when(childService)
                    .mappingCenter(any(), any(), any());
            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, parent.getId(), child.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            //then
            result.andExpect(status().isIAmATeapot())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }
        @Test
        public void 승인요청성공() throws Exception {
            //given
            String url = "/parent/child/center/{childId}/{centerId}";
            doReturn(child)
                    .when(childService)
                    .mappingCenter(any(), any(), any());
            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.patch(url, parent.getId(), child.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            //then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(center.getId())));
        }
    }

    @Nested
    @DisplayName("아이의 시설 탈퇴")
    class exitCenter {
        @Test
        public void 잘못된요청() throws Exception {
            //given
            String url = "/parent/child/center/{childId}";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(childService)
                    .exitCenter(any(), any());
            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(parent)
                            ));
            //then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        public void 정상요청() throws Exception {
            //given
            String url = "/parent/child/center/{childId}";
            //when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(parent)
                            ));
            //then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("아이삭제")
    class deleteChild{
        @Test
        @DisplayName("[error] 잘못된 childId")
        public void childIdError() throws Exception {
            // given
            String url = "/parent/child/{childId}";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(childService)
                    .deleteChild(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }
        @Test
        @DisplayName("[success] 아이 삭제 성공")
        public void 아이삭제성공() throws Exception {
            // given
            String url = "/parent/child/{childId}";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("아이추가")
    class saveChild{
        @Test
        @DisplayName("[error] 불완전한 요청")
        public void 불완전한요청() throws Exception {
            // given
            String url = "/parent/child";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders
                            .multipart(url)
                            .file(multipartFile)
                            .header("Authorization", Creator.createJwtToken(parent))
                            .param("center_id", center.getId().toString())
                            .param("birthDate", LocalDate.now().toString())
            );
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("[error] 잘못된 센터로의 접근")
        public void 잘못된요청() throws Exception {
            // given
            String url = "/parent/child";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(childService)
                    .saveChild(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders
                            .multipart(url)
                            .file(multipartFile)
                            .header("Authorization", Creator.createJwtToken(parent))
                            .param("center_id", center.getId().toString())
                            .param("name", "childName")
                            .param("birthDate", LocalDate.now().toString())
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }
        @Test
        @DisplayName("[success] 아이추가 성공")
        public void 아이추가성공() throws Exception {
            // given
            String url = "/parent/child";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders
                            .multipart(url)
                            .file(multipartFile)
                            .header("Authorization", Creator.createJwtToken(parent))
                            .param("center_id", center.getId().toString())
                            .param("name", "childName")
                            .param("birthDate", LocalDate.now().toString()));

            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("아이 프로필 조회")
    class findChildInfoDetail{
        @Test
        @DisplayName("[error] 잘못된 아이 ID")
        public void 잘못된아이정보() throws Exception {
            // given
            String url = "/parent/child/{childId}";
            UserErrorResult error = UserErrorResult.NOT_VALID_REQUEST;
            doThrow(new UserException(error))
                    .when(childService)
                    .findChildInfoDetail(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 아이 프로필 조회 성공")
        public void 프로필조회성공() throws Exception {
            // given
            String url = "/parent/child/{childId}";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, child.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("시설에 등록된 아이들정보 조회")
    class approvalList{
        @Test
        @DisplayName("[error] 승인되지않은 교사의 요청")
        public void 승인되지않은교사() throws Exception {
            // given
            String url = "/teacher/child/approval";
            UserErrorResult error = UserErrorResult.HAVE_NOT_AUTHORIZATION;
            doThrow(new UserException(error))
                    .when(childService)
                    .findChildApprovalInfoList(any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .header("Authorization", Creator.createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isForbidden())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 아이들정보 조회 성공")
        public void 아이들정보조회성공() throws Exception {
            // given
            String url = "/teacher/child/approval";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .header("Authorization", Creator.createJwtToken(teacher))
            );
            // then
            result.andExpect(status().isOk());
        }
    }


}
