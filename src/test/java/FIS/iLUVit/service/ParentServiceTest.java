package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.SignupParentRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.createmethod.CreateTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static FIS.iLUVit.service.createmethod.CreateTest.createBoard;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParentServiceTest {
    @InjectMocks
    private ParentService target;
    @Mock
    private UserService userService;
    @Mock
    private AuthNumberService authNumberService;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private ScrapRepository scrapRepository;
    @Mock
    private AuthNumberRepository authNumberRepository;

    @Test
    public void 학부모회원가입_성공() {
        // given
        SignupParentRequest request = SignupParentRequest.builder()
                .password("password")
                .passwordCheck("password")
                .loginId("loginId")
                .phoneNum("phoneNum")
                .nickname("nickName")
                .build();
        doReturn("hashedPwd")
                .when(userService)
                .signupValidation(request.getPassword(), request.getPasswordCheck(), request.getLoginId(), request.getPhoneNum(), request.getNickname());
        // when
        Parent result = target.signup(request);
        // then
        assertThat(result.getPassword()).isEqualTo("hashedPwd");
        assertThat(result.getLoginId()).isEqualTo("loginId");
    }
}
