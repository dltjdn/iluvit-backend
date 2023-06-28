package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.scrap.ScrapDirRequest;
import FIS.iLUVit.dto.scrap.ScrapDirDetailRequest;
import FIS.iLUVit.dto.scrap.ScrapDirUpdateRequest;
import FIS.iLUVit.dto.scrap.ScrapInfoDto;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.ScrapErrorResult;
import FIS.iLUVit.exception.ScrapException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ScrapService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ScrapControllerTest {

    @InjectMocks
    private ScrapController target;
    @Mock
    private ScrapService scrapService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private User user;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"), new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();
        user = Creator.createParent(1L);
    }

    @Test
    public void 스크랩폴더목록정보가져오기_성공() throws Exception {
        // given
        String url = "/scrap/dir";
        List<ScrapInfoDto>  response = new ArrayList<>();
        doReturn(response)
                .when(scrapService)
                .findScrapDirList(user.getId());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", Creator.createJwtToken(user))
        );
        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(response)
                ));
    }

    @Nested
    @DisplayName("스크랩폴더추가하기")
    class addScrap{
        @Test
        public void 스크랩폴더추가하기_성공() throws Exception {
            // given
            String url = "/scrap/dir";
            List<ScrapInfoDto> response = new ArrayList<>();
            ScrapDirRequest request = new ScrapDirRequest("name");
            doReturn(response)
                    .when(scrapService)
                    .saveNewScrapDir(user.getId(), request);
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andExpect(status().isOk())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(response)
                    ));
        }

        @Test
        public void 스크랩폴더추가하기_실패_불완전한요청() throws Exception {
            // given
            String url = "/scrap/dir";
            ScrapDirRequest request = new ScrapDirRequest();
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("스크랩 폴더 삭제하기")
    class deleteScrapDir{

        @Test
        @DisplayName("[error] 잘못된스크랩아이디")
        public void 잘못된스크랩아이디() throws Exception {
            // given
            String url = "/scrap/dir/{scrapDirId}";
            ScrapErrorResult errorResult = ScrapErrorResult.NOT_VALID_SCRAP;
            doThrow(new ScrapException(ScrapErrorResult.NOT_VALID_SCRAP))
                    .when(scrapService)
                    .deleteScrapDir(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .param("scrapId", "123")
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(errorResult.getHttpStatus(), errorResult.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 스크랩폴더삭제성공")
        public void 스크랩폴더삭제성공() throws Exception {
            // given
            String url = "/scrap/dir/{scrapDirId}";
            List<ScrapInfoDto> response = new ArrayList<>();
            doReturn(response)
                    .when(scrapService)
                    .deleteScrapDir(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .param("scrapId", "123")
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andExpect(status().isOk());
        }
    }
    @Nested
    @DisplayName("스크랩폴더이름바꾸기")
    class changeScrapName{
        @Test
        @DisplayName("[error] 불완전한요청")
        public void 불완전한요청() throws Exception {
            // given
            String url = "/scrap/dir/name";
            ScrapDirDetailRequest request = new ScrapDirDetailRequest(1L, "");
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.put(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("[error] 잘못된스크랩아이디")
        public void 잘못된스크랩아이디() throws Exception {
            // given
            String url = "/scrap/dir/name";
            ScrapDirDetailRequest request = new ScrapDirDetailRequest(1L, "scrapName");
            ScrapErrorResult error = ScrapErrorResult.NOT_VALID_SCRAP;
            doThrow(new ScrapException(error))
                    .when(scrapService)
                    .modifyScrapDirName(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.put(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 이름바꾸기성공")
        public void 이름바꾸기성공() throws Exception {
            // given
            String url = "/scrap/dir/name";
            ScrapDirDetailRequest request = new ScrapDirDetailRequest(1L, "scrapName");
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.put(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("게시물 스크랩하기")
    class scrapPost{
        @Test
        @DisplayName("[error] 불완전한요청")
        public void 블완전한요청() throws Exception {
            // given
            String url = "/scrap/post/{postId}";
            // ScrapInfoForUpdate scrapInfo = new ScrapInfoForUpdate();
            // ScrapByPostRequest request = new ScrapByPostRequest(1L, List.of(scrapInfo));
            ScrapDirUpdateRequest scrapInfo = new ScrapDirUpdateRequest();
            List<ScrapDirUpdateRequest> scrapInfoList = new ArrayList<>();
            scrapInfoList.add(scrapInfo);

            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.put(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(scrapInfoList))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("[error] 게시물아이디 오류")
        public void 게시물아이디오류() throws Exception {
            // given
            String url = "/scrap/post/{postId}";
            ScrapErrorResult error = ScrapErrorResult.NOT_VALID_POST;
            ScrapDirUpdateRequest scrapInfo = new ScrapDirUpdateRequest(1L, true);
            List<ScrapDirUpdateRequest> scrapInfoList = new ArrayList<>();
            scrapInfoList.add(scrapInfo);


            doThrow(new ScrapException(error))
                    .when(scrapService)
                    .modifyScrapPost(any(), any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.put(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(scrapInfoList))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 스크랩아이디 오류")
        public void 스크랩아이디오류() throws Exception {
            // given
            String url = "/scrap/post/{postId}";
            ScrapErrorResult error = ScrapErrorResult.NOT_VALID_SCRAP;
            ScrapDirUpdateRequest scrapInfo = new ScrapDirUpdateRequest(1L, true);
            List<ScrapDirUpdateRequest> scrapInfoList = new ArrayList<>();
            scrapInfoList.add(scrapInfo);

            doThrow(new ScrapException(error))
                    .when(scrapService)
                    .modifyScrapPost(any(), any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.put(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(scrapInfoList))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 게시물스크랩성공")
        public void 게시물스크랩성공() throws Exception {
            // given
            String url = "/scrap/post/{postId}";
            ScrapDirUpdateRequest scrapInfo = new ScrapDirUpdateRequest(1L, true);
            List<ScrapDirUpdateRequest> scrapInfoList = new ArrayList<>();
            scrapInfoList.add(scrapInfo);

            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.put(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .content(objectMapper.writeValueAsString(scrapInfoList))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("스크랩취소")
    class deleteScrapPost{
        @Test
        @DisplayName("[error] 잘못된 scrapPost")
        public void 잘못된scrapPost() throws Exception {
            // given
            String url = "/scrap/post/{scrapPostId}";
            ScrapErrorResult error = ScrapErrorResult.NOT_VALID_SCRAPPOST;
            doThrow(new ScrapException(error))
                    .when(scrapService)
                    .deleteScrapPost(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .param("scrapPostId", "123")
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 스크랩취소성공")
        public void 스크랩취소성공() throws Exception {
            // given
            String url = "/scrap/post/{scrapPostId}";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url)
                            .header("Authorization", Creator.createJwtToken(user))
                            .param("scrapPostId", "123")
            );
            // then
            result.andExpect(status().isOk());
        }
    }

    @Test
    public void 게시물에대한스크랩목록조회_성공() throws Exception {
        // given
        String url = "/scrap/dir";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", Creator.createJwtToken(user))
                        .param("postId", "123")
        );
        // then
        result.andExpect(status().isOk());
    }

    @Test
    public void 스크램게시물프리뷰_성공() throws Exception {
        // given
        String url = "/scrap/dir";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", Creator.createJwtToken(user))
                        .param("scrapId", "123")
        );
        // then
        result.andExpect(status().isOk());
    }

}