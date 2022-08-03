package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.ParentDetailRequest;
import FIS.iLUVit.controller.dto.ParentDetailResponse;
import FIS.iLUVit.controller.dto.SignupParentRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.junit.jupiter.api.Assertions.*;
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

    @Nested
    @DisplayName("부모 프로필 수정")
    class updateDetail{

        @Test
        @DisplayName("[error] 닉네임 중복")
        public void 닉네임중복() throws JsonProcessingException {
            // given
            ParentDetailRequest request = ParentDetailRequest
                    .builder()
                    .name("name")
                    .nickname("중복닉네임")
                    .changePhoneNum(true)
                    .phoneNum("newPhoneNum")
                    .address("address")
                    .detailAddress("detailAddress")
                    .emailAddress("emailAddress")
                    .interestAge(3)
                    .theme(objectMapper.writeValueAsString(Creator.createTheme()))
                    .build();
            doReturn(Optional.of(parent))
                    .when(parentRepository)
                    .findById(any());
            doReturn(Optional.of(Parent.builder().build()))
                    .when(parentRepository)
                    .findByNickName(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.updateDetail(parent.getId(), request));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.ALREADY_NICKNAME_EXIST);
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

}
