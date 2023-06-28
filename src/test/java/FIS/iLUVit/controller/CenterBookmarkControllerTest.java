package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Prefer;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.PreferErrorResult;
import FIS.iLUVit.exception.PreferException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.CenterBookmarkService;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CenterBookmarkControllerTest {

    MockMvc mockMvc;
    @InjectMocks
    CenterBookmarkController centerBookmarkController;
    @Mock
    private CenterBookmarkService centerBookmarkService;
    ObjectMapper objectMapper;
    Parent parent;
    Center center;
    Prefer prefer;


    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(centerBookmarkController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), new LoginUserArgumentResolver("secretKey"))
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        objectMapper = new ObjectMapper();

        parent = Parent.builder()
                .id(1L)
                .auth(Auth.PARENT)
                .build();

        center = Creator.createCenter(2L, "아이러빗어린이집", true, true, null);

        prefer = Creator.createPrefer(3L, parent, center);
    }

    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    @DisplayName("[success] 즐겨찾기한 시설 목록 조회 성공")
    public void 즐겨찾기한_시설_목록_조회() throws Exception {
        // given
        String url = "/center-bookmark";
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent))
        );
        // then
        result.andExpect(status().isOk());
    }

    @Nested
    @DisplayName("시설 즐겨찾기")
    class savePrefer{
        @Test
        @DisplayName("[error] 이미 즐겨찾기한 시설")
        public void 이미_즐겨찾기한_시설() throws Exception {
            // given
            String url = "/center-bookmark/{centerId}";
            PreferErrorResult error = PreferErrorResult.ALREADY_PREFER;
            doThrow(new PreferException(error))
                    .when(centerBookmarkService)
                    .saveCenterBookmark(any(), any());
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
                    .saveCenterBookmark(any(), any());
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
        @DisplayName("[success] 시설 즐겨찾기 성공")
        public void 시설_즐겨찾기_성공() throws Exception {
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
    @DisplayName("시설 즐겨찾기 해제")
    class deletePrefer{
        @Test
        @DisplayName("[error] 유효하지 않은 시설정보")
        public void 시설_정보_오류() throws Exception {
            // given
            String url = "/center-bookmark/{centerId}";
            PreferErrorResult error = PreferErrorResult.NOT_VALID_CENTER;
            doThrow(new PreferException(error))
                    .when(centerBookmarkService)
                    .deleteCenterBookmark(any(), any());
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
        @DisplayName("[success] 즐겨찾기 해제 성공")
        public void 시설_즐겨찾기_해제성공() throws Exception {
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
