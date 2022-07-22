package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.WaitingRegisterDto;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.WaitingService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WaitingControllerTest {

    @Mock
    WaitingService waitingService;

    @InjectMocks
    WaitingController target;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    User user;

    @BeforeEach
    void init(){
        // Controller
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        objectMapper = new ObjectMapper();

        user = Parent.builder()
                .id(1L)
                .auth(Auth.PARENT)
                .build();
    }

    public String createJwtToken(){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("symmetricKey"));
    }

    @Nested
    @DisplayName("설명회 대기 신청")
    class 설명회대기신청{

        @Test
        @DisplayName("[error] 로그인이 안되어 있을 경우 Error 발생")
        public void 로그인X() throws Exception {
            //given
            final String url = "/waiting";
            //when
            ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(objectMapper.writeValueAsString(new WaitingRegisterDto(1L)))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.FORBIDDEN
                                    , "인증된 사용자가 아닙니다")
                    )));
        }
        
        @Test
        @DisplayName("[error] 잘못 요청시 오류 발생")
        public void 잘못된ptDateId요청() throws Exception {
            //given
            final String url = "/waiting";
            String jwtToken = createJwtToken();
            //when
            ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(objectMapper.writeValueAsString(new WaitingRegisterDto(-1L)))
                            .header(HttpHeaders.AUTHORIZATION, jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.BAD_REQUEST
                                    , "[올바르지 않은 ptDateId 입니다]")
                    )));
        }

        @Test
        @DisplayName("[error] 서비스 계층 오류 잘못된 ptDate 요청")
        public void 잘못된ptDate요청() throws Exception {
            //given
            final String url = "/waiting";
            String jwtToken = createJwtToken();
            Mockito.doThrow(new PresentationException(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST))
                    .when(waitingService).register(1L, 1L);

            //when
            ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(objectMapper.writeValueAsString(new WaitingRegisterDto(1L)))
                            .header(HttpHeaders.AUTHORIZATION, jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.BAD_REQUEST
                                    , "올바르지 않은 ptDateId 입니다")
                    )));
            verify(waitingService, times(1)).register(1L, 1L);
        }

        @Test
        @DisplayName("[error] 설명회 신청기간이 지났을 경우")
        public void 설명회신청기간이지남() throws Exception {
            //given
            final String url = "/waiting";
            String jwtToken = createJwtToken();
            Mockito.doThrow(new PresentationException(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED))
                    .when(waitingService).register(1L, 1L);

            //when
            ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(objectMapper.writeValueAsString(new WaitingRegisterDto(1L)))
                            .header(HttpHeaders.AUTHORIZATION, jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.BAD_REQUEST
                                    , "설명회 신청기간이 종료되었습니다")
                    )));
            verify(waitingService, times(1)).register(1L, 1L);
        }

        @Test
        @DisplayName("[error] 대기등록을 이미 했을 경우")
        public void 대기등록을이미했을경우() throws Exception {
            //given
            final String url = "/waiting";
            String jwtToken = createJwtToken();
            Mockito.doThrow(new PresentationException(PresentationErrorResult.PARTICIPATION_PERIOD_PASSED))
                    .when(waitingService).register(1L, 1L);

            //when
            ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(objectMapper.writeValueAsString(new WaitingRegisterDto(1L)))
                            .header(HttpHeaders.AUTHORIZATION, jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(HttpStatus.BAD_REQUEST
                                    , "설명회 신청기간이 종료되었습니다")
                    )));
            verify(waitingService, times(1)).register(1L, 1L);
        }
    }


}
