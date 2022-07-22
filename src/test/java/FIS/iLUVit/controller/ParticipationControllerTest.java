package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.ParticipationRegisterRequestDto;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ParticipationService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ParticipationControllerTest {

    @Mock
    ParticipationService participationService;
    @InjectMocks
    ParticipationController participationController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    User user;

    @BeforeEach
    void init(){
        // Controller
        mockMvc = MockMvcBuilders.standaloneSetup(participationController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

        objectMapper = new ObjectMapper();

        user = Parent.builder()
                .id(1L)
                .auth(Auth.PARENT)
                .build();
    }

    private ParticipationRegisterRequestDto participationRegisterRequestDto(Long ptDateId){
        return new ParticipationRegisterRequestDto(ptDateId);
    }

    public String createJwtToken(){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("symmetricKey"));
    }

    @Test
    public void 설명회_신청_로그인안함() throws Exception {
        //given
        final String url = "/participation";
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(participationRegisterRequestDto(1L)))
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
    public void 설명회_신청_설명회_회차_아이디값_음수() throws Exception {
        //given
        final String url = "/participation";
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(objectMapper.writeValueAsString(participationRegisterRequestDto(-1L)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(HttpStatus.BAD_REQUEST, "[잘못된 설명회 회차 아이디 입니다]"))
                        ));
    }

    // validation, arguments 들에 대해서 예외 처리

    @Test
    public void 설명회_수용인원_초과() throws Exception {
        //given
        final String url = "/participation";
        final PresentationErrorResult error = PresentationErrorResult.PRESENTATION_OVERCAPACITY;

        Mockito.doThrow(new PresentationException(error))
                .when(participationService)
                .register(any(Long.class), any(Long.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(participationRegisterRequestDto(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then => controller Advice 다시 재정의
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                            new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 설명회_신청_이미_신청한_사용자() throws Exception {
        //given
        final String url = "/participation";
        PresentationErrorResult error = PresentationErrorResult.ALREADY_PARTICIPATED_IN;

        Mockito.doThrow(new PresentationException(error))
                .when(participationService)
                .register(any(Long.class), any(Long.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(participationRegisterRequestDto(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 설명회_신청기간_지남() throws Exception {
        //given
        final String url = "/participation";
        PresentationErrorResult error = PresentationErrorResult.PARTICIPATION_PERIOD_PASSED;

        Mockito.doThrow(new PresentationException(error))
                .when(participationService)
                .register(any(Long.class), any(Long.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(participationRegisterRequestDto(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                new ErrorResponse(error.getHttpStatus(), error.getMessage())
                        ))
                );
    }

    @Test
    public void 설명회_신청_성공() throws Exception {
        //given
        final String url = "/participation";
        final PresentationErrorResult error = PresentationErrorResult.PRESENTATION_OVERCAPACITY;

        Mockito.doReturn(1L)
                .when(participationService)
                .register(any(Long.class), any(Long.class));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", createJwtToken())
                        .content(objectMapper.writeValueAsString(participationRegisterRequestDto(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(
                                1L
                        ))
                );
    }

}