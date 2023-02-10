package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.PreferErrorResult;
import FIS.iLUVit.exception.PreferException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.CenterBookmarkService;
import FIS.iLUVit.service.ParentService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CenterBookmarkControllerTest {

    @InjectMocks
    CenterController target;
    @Mock
    private ParentService parentService;
    @Mock
    private CenterBookmarkService centerBookmarkService;
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    private Parent parent;
    private Center center;


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
    public void 찜한시설리스트() throws Exception {
        // given
        String url = "/center-bookmark";
        Parent parent = Creator.createParent();
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent))
        );
        // then
        result.andExpect(status().isOk());
    }

    @Nested
    @DisplayName("시설찜하기")
    class savePrefer{
        @Test
        @DisplayName("[error] 이미 찜한 시설")
        public void 이미찜한시설() throws Exception {
            // given
            String url = "/center-bookmark/{centerId}";
            PreferErrorResult error = PreferErrorResult.ALREADY_PREFER;
            doThrow(new PreferException(error))
                    .when(centerBookmarkService)
                    .savePrefer(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, center.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[error] 잘못된 시설")
        public void centerIdError() throws Exception {
            // given
            String url = "/center-bookmark/{centerId}";
            PreferErrorResult error = PreferErrorResult.NOT_VALID_CENTER;
            doThrow(new PreferException(error))
                    .when(centerBookmarkService)
                    .savePrefer(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, center.getId())
                            .header("Authorization", parent.getId())
            );
            // then
            result.andExpect(status().isIAmATeapot())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 찜하기 성공")
        public void 찜성공() throws Exception {
            // given
            String url = "/center-bookmark/{centerId}";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, center.getId())
                            .header("Authorization", parent.getId()));
            // then
            result.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("시설 찜 해제")
    class deletePrefer{
        @Test
        @DisplayName("[error] 유효하지 않은 시설정보")
        public void 시설정보오류() throws Exception {
            // given
            String url = "/center-bookmark/{centerId}";
            PreferErrorResult error = PreferErrorResult.NOT_VALID_CENTER;
            doThrow(new PreferException(error))
                    .when(centerBookmarkService)
                    .deletePrefer(any(), any());
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, center.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            // then
            result.andExpect(status().isIAmATeapot())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(new ErrorResponse(error.getHttpStatus(), error.getMessage()))
                    ));
        }

        @Test
        @DisplayName("[success] 찜 해제 성공")
        public void 찜해제성공() throws Exception {
            // given
            String url = "/center-bookmark/{centerId}";
            // when
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, center.getId())
                            .header("Authorization", Creator.createJwtToken(parent))
            );
            // then
            result.andExpect(status().isOk());
        }
    }

}
