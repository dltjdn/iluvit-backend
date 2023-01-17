package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.ParentDetailRequest;
import FIS.iLUVit.controller.dto.ParentDetailResponse;
import FIS.iLUVit.controller.dto.SignupParentRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Prefer;
import FIS.iLUVit.exception.PreferErrorResult;
import FIS.iLUVit.exception.PreferException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParentServiceTest {
    @InjectMocks
    private ParentService target;

    @InjectMocks
    private CenterBookmarkService centerBookmarkService;
    @Mock
    private UserService userService;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private BoardBookmarkRepository boardBookmarkRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private CenterRepository centerRepository;
    @Mock
    private CenterBookmarkRepository centerBookmarkRepository;
    @Mock
    private MapService mapService;




    private ObjectMapper objectMapper;
    private Parent parent1;
    private Parent parent2;
    private Center center1;
    private Center center2;
    private Prefer prefer1;
    private Board board1;

    @BeforeEach
    public void init() {
        parent1 = Creator.createParent(1L, "phoneNum1", "parent1", "parent1");
        parent2 = Creator.createParent(2L, "phoneNum2", "parent2", "parent2");
        center1 = Creator.createCenter(3L, "center1");
        center2 = Creator.createCenter(4L, "center2");
        prefer1 = Creator.createPrefer(5L, parent1, center1);
        board1 = Creator.createBoard(6L, "board1", null, true);
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
        doReturn(List.of(board1))
                .when(boardRepository)
                .findDefaultByModu();
        Mockito.doReturn(Pair.of(126.8806602, 37.4778951))
                .when(mapService).convertAddressToLocation(null);
        Mockito.doReturn(Pair.of("서울특별시", "금천구"))
                .when(mapService).getSidoSigunguByLocation(126.8806602, 37.4778951);
        // when
        Parent result = target.signup(request);
        // then
        assertThat(result.getPassword()).isEqualTo("hashedPwd");
        assertThat(result.getLoginId()).isEqualTo("loginId");
        assertThat(result.getReadAlarm()).isEqualTo(true);
        verify(boardBookmarkRepository, times(1)).save(any());
    }

    @Test
    public void 부모프로필정보조회_성공() throws IOException {
        // given
        doReturn(Optional.of(parent1))
                .when(parentRepository)
                .findById(parent1.getId());
        doReturn("imagePath")
                .when(imageService)
                .getProfileImage(parent1);
        // when
        ParentDetailResponse result = target.findDetail(parent1.getId());
        // then
        assertThat(result.getNickname()).isEqualTo(parent1.getNickName());
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
            doReturn(Optional.of(parent1))
                    .when(parentRepository)
                    .findById(any());
            doReturn(Optional.of(Parent.builder().build()))
                    .when(parentRepository)
                    .findByNickName(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.updateDetail(parent1.getId(), request));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.ALREADY_NICKNAME_EXIST);
        }

        @Test
        public void 부모프로필정보수정_성공_번호변경X() throws IOException {
            // given
            doReturn(Optional.of(parent1))
                    .when(parentRepository)
                    .findById(any());
            ParentDetailRequest request = ParentDetailRequest
                    .builder()
                    .name("name")
                    .nickname("nickName")
                    .changePhoneNum(false)
                    .phoneNum("invalidPhoneNum")
                    .address("서울특별시 금천구 가산동 429-1 가산디지털2로 108")
                    .detailAddress("detailAddress")
                    .emailAddress("emailAddress")
                    .interestAge(3)
                    .theme(objectMapper.writeValueAsString(Creator.createTheme()))
                    .build();
            Mockito.doReturn(Pair.of(126.8806602, 37.4778951))
                    .when(mapService).convertAddressToLocation(anyString());
            Mockito.doReturn(Pair.of("서울특별시", "금천구"))
                    .when(mapService).getSidoSigunguByLocation(126.8806602, 37.4778951);
            // when
            ParentDetailResponse result = target.updateDetail(parent1.getId(), request);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getAddress()).isEqualTo(request.getAddress());
            assertThat(result.getPhoneNumber()).isNotEqualTo(request.getPhoneNum());
        }

        @Test
        public void 부모프로필정보수정_성공_번호변경O() throws IOException {
            // given
            doReturn(Optional.of(parent1))
                    .when(parentRepository)
                    .findById(parent1.getId());
            ParentDetailRequest request = ParentDetailRequest
                    .builder()
                    .name("name")
                    .nickname("nickName")
                    .changePhoneNum(true)
                    .phoneNum("newPhoneNum")
                    .address("서울특별시 금천구 가산동 429-1 가산디지털2로 108")
                    .detailAddress("detailAddress")
                    .emailAddress("emailAddress")
                    .interestAge(3)
                    .theme(objectMapper.writeValueAsString(Creator.createTheme()))
                    .build();
            Mockito.doReturn(Pair.of(126.8806602, 37.4778951))
                    .when(mapService).convertAddressToLocation(anyString());
            Mockito.doReturn(Pair.of("서울특별시", "금천구"))
                    .when(mapService).getSidoSigunguByLocation(126.8806602, 37.4778951);
            // when
            ParentDetailResponse result = target.updateDetail(parent1.getId(), request);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getNickname()).isEqualTo("nickName");
            assertThat(result.getPhoneNumber()).isEqualTo("newPhoneNum");
        }

    }

    @Nested
    @DisplayName("시설 찜하기")
    class savePrefer{
        @Test
        @DisplayName("[error] 이미 찜한 시설")
        public void 이미찜한시설() {
            // given
            doReturn(Optional.of(prefer1))
                    .when(centerBookmarkRepository)
                    .findByUserIdAndCenterId(parent1.getId(), center1.getId());
            // when
            PreferException result = assertThrows(PreferException.class,
                    () -> centerBookmarkService.savePrefer(parent1.getId(), center1.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(PreferErrorResult.ALREADY_PREFER);
        }
        
        @Test
        @DisplayName("[error] 잘못된 시설을 찜")
        public void 잘못된시설() {
            // given
            doReturn(Optional.empty())
                    .when(centerBookmarkRepository)
                    .findByUserIdAndCenterId(parent1.getId(), center2.getId());
            doReturn(center2)
                    .when(centerRepository)
                    .getById(any());
            doReturn(parent1)
                    .when(parentRepository)
                    .getById(parent1.getId());
            doThrow(new DataIntegrityViolationException("해당시설없음"))
                    .when(centerBookmarkRepository)
                    .saveAndFlush(any());
            // when
            PreferException result = assertThrows(PreferException.class,
                    () -> centerBookmarkService.savePrefer(parent1.getId(), center2.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(PreferErrorResult.NOT_VALID_CENTER);
        }

        @Test
        @DisplayName("[success] 시설 찜하기 성공")
        public void 찜하기성공() {
            // given
            doReturn(Optional.empty())
                    .when(centerBookmarkRepository)
                    .findByUserIdAndCenterId(parent1.getId(), center2.getId());
            doReturn(center2)
                    .when(centerRepository)
                    .getById(any());
            doReturn(parent1)
                    .when(parentRepository)
                    .getById(parent1.getId());
            // when
            Prefer result = centerBookmarkService.savePrefer(parent1.getId(), center2.getId());
            // then
            assertThat(result.getParent().getId()).isEqualTo(parent1.getId());
            assertThat(result.getCenter().getId()).isEqualTo(center2.getId());
        }
    }

    @Nested
    @DisplayName("시설 찜 해제하기")
    class deletePrefer{
        @Test
        @DisplayName("[error] 찜하지 않은 시설")
        public void 찜하지않은시설() {
            // given
            doReturn(Optional.empty())
                    .when(centerBookmarkRepository)
                    .findByUserIdAndCenterId(any(), any());
            // when
            PreferException result = assertThrows(PreferException.class,
                    () -> centerBookmarkService.deletePrefer(parent1.getId(), center1.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(PreferErrorResult.NOT_VALID_CENTER);
        }

        @Test
        @DisplayName("[success] 찜 해제하기 성공")
        public void 찜해제하기성공() {
            // given
            doReturn(Optional.of(prefer1))
                    .when(centerBookmarkRepository)
                    .findByUserIdAndCenterId(parent1.getId(), center1.getId());
            // when
            centerBookmarkService.deletePrefer(parent1.getId(), center1.getId());
            // then
            verify(centerBookmarkRepository, times(1)).delete(any());
        }
    }

}
