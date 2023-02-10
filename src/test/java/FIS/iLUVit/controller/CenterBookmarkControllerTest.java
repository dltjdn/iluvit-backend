package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CenterBookmarkControllerTest {

    @InjectMocks
    CenterController target;

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
}
