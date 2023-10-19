package FIS.iLUVit.document;

import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.user.controller.UserController;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.dto.UserBasicInfoResponse;
import FIS.iLUVit.domain.user.service.UserService;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.global.config.argumentResolver.LoginUserArgumentResolver;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@Import(RestDocsConfiguration.class)
public class UserDocumentationTest {

    @InjectMocks
    private UserController target;
    @Mock
    private UserService userService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Parent parent;

    @BeforeEach
    public void init(final RestDocumentationContextProvider provider) throws IOException {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"))
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .build();
        parent = Parent.builder()
                .id(1L)
                .nickName("nickname")
                .auth(Auth.PARENT)
                .build();
    }

    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void findOneUser() throws Exception {
        // given
        String url = "/user";
        UserBasicInfoResponse userResponse = new UserBasicInfoResponse(1L, "nickname", Auth.PARENT);

        doReturn(userResponse)
                .when(userService)
                .findUserDetails(any());
        // when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent))
        );
        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(userResponse)
                ))
                .andDo(document("find-one-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("유저 기본키"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                                fieldWithPath("auth").type(JsonFieldType.STRING).description("유저 권한 - PARENT, TEACHER, DIRECTOR")
                        ))
                );
    }

}
