package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.CenterApprovalAcceptedAlarm;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChildServiceTest {
    @InjectMocks
    private ChildService target;
    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private ImageService imageService;
    @Mock
    private ChildRepository childRepository;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private CenterRepository centerRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;

    private Parent parent1;
    private Parent parent2;
    private Center center1;
    private Center center2;
    private Child child1;
    private Child child2;
    private Child child3;
    private Child child4;
    private Child child5;
    private Teacher director;
    private Teacher teacher1;
    private Teacher teacher2;
    private Board board1;
    private Board board2;
    private Board board3;
    private Bookmark bookmark;
    private MockMultipartFile multipartFile;

    @BeforeEach
    public void init() throws IOException {
        parent1 = Creator.createParent(1L);
        parent2 = Creator.createParent(2L);
        center1 = Creator.createCenter(3L, "center1");
        center2 = Creator.createCenter(14L, "center2");
        child1 = Creator.createChild(4L, "child1", parent1, center1, Approval.ACCEPT);
        child2 = Creator.createChild(5L, "child2", parent1, center1, Approval.WAITING);
        child3 = Creator.createChild(9L, "child3", parent1, center1, Approval.REJECT);
        child4 = Creator.createChild(10L, "child4", parent2, center1, Approval.WAITING);
        child5 = Creator.createChild(15L, "child5", parent2, center2, Approval.WAITING);
        director = Creator.createTeacher(7L, "director", center1, Auth.DIRECTOR, Approval.ACCEPT);
        teacher1 = Creator.createTeacher(6L, "teacher1", center1, Auth.TEACHER, Approval.ACCEPT);
        teacher2 = Creator.createTeacher(8L, "teacher2", center1, Auth.TEACHER, Approval.WAITING);
        board1 = Creator.createBoard(11L, "board1", center1, true);
        board2 = Creator.createBoard(12L, "board2", center1, true);
        board3 = Creator.createBoard(13L, "board3", center1, false);
        bookmark = Creator.createBookmark(16L, board1, parent1);
        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile(name, name, "image", content);
    }

    @Nested
    @DisplayName("부모 메인페이지 아이들정보")
    class 아이들정보{
        @Test
        public void 아이여러명() {
            // given
            parent1.getChildren().add(child1);
            parent1.getChildren().add(child2);
            doReturn(Optional.of(parent1))
                    .when(parentRepository)
                    .findWithChildren(parent1.getId());
            doReturn("imagePath")
                    .when(imageService)
                    .getProfileImage(any(Child.class));
            // when
            ChildInfoDTO result = target.childrenInfo(parent1.getId());
            // then
            assertThat(result.getData().size()).isEqualTo(2);
        }
        @Test
        public void 아이없음() {
            // given
            doReturn(Optional.of(parent2))
                    .when(parentRepository)
                    .findWithChildren(any());
            ChildInfoDTO result = target.childrenInfo(parent2.getId());
            // then
            assertThat(result.getData().size()).isEqualTo(0);
        }
    }

    @Test
    public void 학부모관리페이지조회() {
        // given
        center1.getChildren().add(child1);
        center1.getChildren().add(child2);
        center1.getChildren().add(child3);
        center1.getChildren().add(child4);
        doReturn(Optional.of(director))
                .when(teacherRepository)
                .findByIdWithCenterWithChildWithParent(director.getId());
        doReturn("imagePath")
                .when(imageService)
                .getProfileImage(any(Child.class));
        // when
        ChildApprovalListResponse result = target.findChildApprovalInfoList(director.getId());
        // then
        assertThat(result.getData().size()).isEqualTo(3);
    }

    @Nested
    @DisplayName("아이/학부모 승인")
    class acceptChild {
        @Test
        @DisplayName("[error] 승인받지않은교사의요청")
        public void 승인받지않은교사의요청() {
            // given
            center1.getChildren().add(child1);
            center1.getChildren().add(child2);
            center1.getChildren().add(child3);
            center1.getChildren().add(child4);
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findByIdWithCenterWithChildWithParent(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.acceptChild(teacher2.getId(), child2.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }

        @Test
        @DisplayName("[error] 아이 아이디 잘못됨")
        public void 아이아이디잘못됨() {
            // given
            center1.getChildren().add(child1);
            center1.getChildren().add(child2);
            center1.getChildren().add(child3);
            center1.getChildren().add(child4);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findByIdWithCenterWithChildWithParent(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.acceptChild(teacher1.getId(), child1.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }

        @Test
        @DisplayName("[success] 부모의아이 최초승인")
        public void 최초승인() {
            try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
                // given
                center1.getChildren().add(child1);
                center1.getChildren().add(child2);
                center1.getChildren().add(child3);
                center1.getChildren().add(child4);
                center1.getBoards().add(board1);
                center1.getBoards().add(board2);
                center1.getBoards().add(board3);
                doReturn(Optional.of(teacher1))
                        .when(teacherRepository)
                        .findByIdWithCenterWithChildWithParent(any());
                parent2.getChildren().add(child4);
                doReturn(Optional.of(parent2))
                        .when(parentRepository)
                        .findByIdWithChild(any());
                alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                        .thenReturn("설명회가 가득 찼습니다");
                AlarmEvent alarmEvent = new AlarmEvent(new CenterApprovalAcceptedAlarm(Parent.builder().build(), Center.builder().build()));
                alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(new CenterApprovalAcceptedAlarm(parent2, teacher1.getCenter())))
                        .thenReturn(alarmEvent);
                // when
                Child result = target.acceptChild(director.getId(), child4.getId());
                // then
                assertThat(result.getId()).isEqualTo(child4.getId());
                assertThat(result.getApproval()).isEqualTo(Approval.ACCEPT);
                verify(bookmarkService, times(2)).create(any(), any());
            }
        }

        @Test
        @DisplayName("[success] 같은시설 중복승인")
        public void 중복승인() {
            try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
                // given
                center1.getChildren().add(child1);
                center1.getChildren().add(child2);
                center1.getChildren().add(child3);
                center1.getChildren().add(child4);
                center1.getBoards().add(board1);
                center1.getBoards().add(board2);
                center1.getBoards().add(board3);
                doReturn(Optional.of(teacher1))
                        .when(teacherRepository)
                        .findByIdWithCenterWithChildWithParent(any());
                parent1.getChildren().add(child1);
                parent1.getChildren().add(child2);
                parent1.getChildren().add(child3);
                doReturn(Optional.of(parent1))
                        .when(parentRepository)
                        .findByIdWithChild(any());
                alarmUtils.when(() -> AlarmUtils.getMessage(any(String.class), any(Object[].class)))
                        .thenReturn("설명회가 가득 찼습니다");
                AlarmEvent alarmEvent = new AlarmEvent(new CenterApprovalAcceptedAlarm(Parent.builder().build(), Center.builder().build()));
                alarmUtils.when(() -> AlarmUtils.publishAlarmEvent(new CenterApprovalAcceptedAlarm(parent1, teacher1.getCenter())))
                        .thenReturn(alarmEvent);
                // when
                Child result = target.acceptChild(director.getId(), child2.getId());
                // then
                assertThat(result.getId()).isEqualTo(child2.getId());
                assertThat(result.getApproval()).isEqualTo(Approval.ACCEPT);
                verify(bookmarkService, times(0)).create(any(), any());
            }
        }
    }
    @Nested
    @DisplayName("아이/학부모 삭제/거절")
    class fireChild{
        @Test
        @DisplayName("[error] 승인받지않은 교사")
        public void 승인받지않은교사() {
            // given
            doReturn(Optional.empty())
                    .when(teacherRepository)
                    .findByIdWithCenterWithChildWithParent(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.fireChild(teacher2.getId(), child1.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.HAVE_NOT_AUTHORIZATION);
        }

        @Test
        @DisplayName("[error] 아이 아이디 에러")
        public void 아이아이디에러() {
            // given
            center1.getChildren().add(child1);
            center1.getChildren().add(child2);
            center1.getChildren().add(child3);
            center1.getChildren().add(child4);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findByIdWithCenterWithChildWithParent(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.fireChild(teacher1.getId(), child5.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }

        @Test
        @DisplayName("[success] 마지막 아이 삭제 성공")
        public void 마지막아이삭제성공() {
            // given
            center1.getChildren().add(child1);
            center1.getChildren().add(child2);
            center1.getChildren().add(child3);
            center1.getChildren().add(child4);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findByIdWithCenterWithChildWithParent(any());
            doReturn(List.of(child1, child2, child3))
                    .when(childRepository)
                    .findByUserWithCenter(any());
            // when
            target.fireChild(teacher1.getId(), child1.getId());
            // then
            assertThat(child1.getApproval()).isEqualTo(Approval.REJECT);
            verify(boardRepository, times(1)).findByCenter(any());
            verify(bookmarkRepository, times(1)).deleteAllByBoardAndUser(any(), any());
        }

        @Test
        @DisplayName("[success] 아이 삭제 성공 아직 더 있음")
        public void 아이삭제성공아직더있음() {
            // given
            child2.accepted();
            center1.getChildren().add(child1);
            center1.getChildren().add(child2);
            center1.getChildren().add(child3);
            center1.getChildren().add(child4);
            doReturn(Optional.of(teacher1))
                    .when(teacherRepository)
                    .findByIdWithCenterWithChildWithParent(any());
            doReturn(List.of(child1, child2, child3))
                    .when(childRepository)
                    .findByUserWithCenter(any());
            // when
            target.fireChild(teacher1.getId(), child1.getId());
            // then
            assertThat(child1.getApproval()).isEqualTo(Approval.REJECT);
            verify(boardRepository, times(0)).findByCenter(any());
            verify(bookmarkRepository, times(0)).deleteAllByBoardAndUser(any(), any());
        }
    }


    @Nested
    @DisplayName("아이 추가")
    class saveChild{
        @Test
        @DisplayName("[error] 시설정보 잘못됨")
        public void 시설정보에러() {
            // given
            SaveChildRequest request = new SaveChildRequest(321L, "name", LocalDate.now(), multipartFile);
            doReturn(parent1)
                    .when(parentRepository)
                    .getById(any());
            doReturn(Optional.empty())
                    .when(centerRepository)
                    .findByIdAndSignedWithTeacher(any());
            // when
            UserException result = assertThrows(UserException.class,
                    () -> target.saveChild(parent1.getId(), request));
            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }

        @Test
        @DisplayName("[success] 아이추가 성공")
        public void 아이추가성공() throws IOException {
            try (MockedStatic<AlarmUtils> alarmUtils = Mockito.mockStatic(AlarmUtils.class)) {
                // given
                center1.getTeachers().add(director);
                center1.getTeachers().add(teacher1);
                center1.getTeachers().add(teacher2);
                SaveChildRequest request = new SaveChildRequest(center1.getId(), "name", LocalDate.now(), multipartFile);
                doReturn(parent1)
                        .when(parentRepository)
                        .getById(any());
                doReturn(Optional.of(center1))
                        .when(centerRepository)
                        .findByIdAndSignedWithTeacher(request.getCenter_id());
                // when
                Child result = target.saveChild(parent1.getId(), request);
                // then
                assertThat(result).isNotNull();
                assertThat(result.getName()).isEqualTo("name");
                assertThat(result.getParent().getId()).isEqualTo(parent1.getId());
                assertThat(result.getCenter().getId()).isEqualTo(center1.getId());
            }
        }
    }
    @Nested
    @DisplayName("아이 프로필 조회")
    class findChildInfoDetail{
        @Test
        public void 잘못된아이아이디() throws Exception {
            //given
            doReturn(Optional.empty())
                    .when(childRepository)
                    .findByIdWithParentAndCenter(any(), any());
            //when
            UserException result = assertThrows(UserException.class,
                    () -> target.findChildInfoDetail(parent1.getId(), child4.getId(), PageRequest.of(0, 10)));
            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_VALID_REQUEST);
        }
        @Test
        public void 조회성공() throws Exception {
            //given
            doReturn(Optional.of(child1))
                    .when(childRepository)
                    .findByIdWithParentAndCenter(any(), any());
            doReturn("imagePath")
                    .when(imageService)
                    .getProfileImage(any(Child.class));
            List<CenterInfoDto> content = List.of(CenterInfoDto.builder().build());
            SliceImpl<CenterInfoDto> slice = new SliceImpl<>(content, PageRequest.of(0, 10), false);
            doReturn(slice)
                    .when(centerRepository)
                    .findCenterForAddChild(any(), any(), any(), any());
            //when
            ChildInfoDetailResponse result = target.findChildInfoDetail(parent1.getId(), child1.getId(), PageRequest.of(0, 10));
            //then
            assertThat(result.getChild_id()).isEqualTo(child1.getId());
            assertThat(result.getCenter_name()).isEqualTo(center1.getName());
        }
    }
    
}
