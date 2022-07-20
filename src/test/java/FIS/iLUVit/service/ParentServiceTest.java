package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.ParentDetailRequest;
import FIS.iLUVit.controller.dto.ParentDetailResponse;
import FIS.iLUVit.controller.dto.SignupParentRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Mock
    private ImageService imageService;

    private ObjectMapper objectMapper;
    private Parent parent;

    @BeforeEach
    public void init() {
        parent = Creator.createParent("phoneNum");
        objectMapper = new ObjectMapper();
    }

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

    @Test
    public void 부모프로필정보조회_성공() throws IOException {
        // given
        doReturn(Optional.of(parent))
                .when(parentRepository)
                .findById(parent.getId());
        doReturn("imagePath")
                .when(imageService)
                .getProfileImage(parent);
        // when
        ParentDetailResponse result = target.findDetail(parent.getId());
        // then
        assertThat(result.getNickname()).isEqualTo(parent.getNickName());
        assertThat(result.getProfileImg()).isEqualTo("imagePath");
    }

    @Test
    public void 부모프로필정보수정_성공() throws IOException {
        // given
        doReturn(Optional.of(parent))
                .when(parentRepository)
                .findById(parent.getId());
        ParentDetailRequest request = ParentDetailRequest
                .builder()
                .name("name")
                .nickname("nickName")
                .changePhoneNum(true)
                .phoneNum("newPhoneNum")
                .address("address")
                .detailAddress("detailAddress")
                .emailAddress("emailAddress")
                .interestAge(3)
                .theme(objectMapper.writeValueAsString(Creator.createTheme()))
                .build();
        // when
        ParentDetailResponse result = target.updateDetail(parent.getId(), request);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo("nickName");
        assertThat(result.getPhoneNumber()).isEqualTo("newPhoneNum");
    }

}
