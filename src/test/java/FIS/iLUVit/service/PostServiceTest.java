package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.GetPostResponse;
import FIS.iLUVit.controller.dto.GetPostResponsePreview;
import FIS.iLUVit.controller.dto.PostRegisterRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    PostService postService;

    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ImageService imageService;
    @Mock
    BoardRepository boardRepository;
    @Mock
    CenterRepository centerRepository;
    @Mock
    BookmarkRepository bookmarkRepository;
    @Mock
    ScrapPostRepository scrapPostRepository;
    @Mock
    PostHeartRepository postHeartRepository;
    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    AlarmRepository alarmRepository;

    ObjectMapper objectMapper;

    Center center1;
    Center center2;
    Center center3;

    Board board1;
    Board board2;
    Board board3;
    Board board4;
    Board board5;

    Post post1;
    Post post2;
    Post post3;
    Post post4;
    Post post5;
    Post post6;
    Post post7;
    Post post8;
    Post post9;
    Post post10;
    Post post11;
    Post post12;
    Post post13;

    Child child1;
    Child child2;
    Child child3;

    Parent parent1;
    Parent parent2;
    Parent parent3;

    Teacher teacher1;
    Teacher teacher2;

    MultipartFile multipartFile;
    List<MultipartFile> multipartFileList = new ArrayList<>();
    PostRegisterRequest postRegisterRequest = new PostRegisterRequest();

    @BeforeEach
    public void init() throws IOException {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        center1 = Creator.createCenter(1L, "팡팡유치원", true, true, null);
        center2 = Creator.createCenter(2L, "가산유치원", true, true, null);
        center3 = Creator.createCenter(3L,"디지털유치원", true, true, null);

        child1 = Child.createChild("childA", null, null, parent1);
        child2 = Child.createChild("childB", null, null, parent1);
        child3 = Child.createChild("childC", null, null, parent1);
        child1.mappingCenter(center1);
        child2.mappingCenter(center2);
        child3.mappingCenter(center3);

        parent1 = Parent.builder()
                .id(4L)
                .name("ParentA")
                .auth(Auth.PARENT)
                .build();
        parent1.getChildren().add(child1);
        parent1.getChildren().add(child2);
        parent1.getChildren().add(child3);


        parent2 = Parent.builder()
                .id(5L)
                .name("ParentB")
                .auth(Auth.PARENT)
                .build();
        parent3 = Parent.builder()
                .id(6L)
                .name("ParentC")
                .auth(Auth.PARENT)
                .build();
        teacher1 = Teacher.builder()
                .id(7L)
                .center(center1)
                .approval(Approval.WAITING)
                .name("TeacherA")
                .auth(Auth.TEACHER)
                .build();
        teacher2 = Teacher.builder()
                .id(8L)
                .center(null)
                .approval(Approval.ACCEPT)
                .name("TeacherB")
                .auth(Auth.TEACHER)
                .build();

        board1 = CreateTest.createBoard(9L, "공지게시판", BoardKind.NOTICE, center1, false);
        board2 = CreateTest.createBoard(10L,"자유게시판", BoardKind.NORMAL, center1, true);
        board3 = CreateTest.createBoard(11L,"정보게시판", BoardKind.NORMAL, center1, true);
        board4 = CreateTest.createBoard(12L,"장터게시판", BoardKind.NORMAL, center2, true);
        board5 = CreateTest.createBoard(13L,"영상게시판", BoardKind.NORMAL, center3, true);

        post1 = Creator.createPost(14L, "제목1", "내용1", true, board1, parent1);
        post2 = Creator.createPost(15L, "제목2", "내용2", true, board1, parent1);
        post3 = Creator.createPost(16L, "제목3", "내용3", true, board1, parent1);
        post4 = Creator.createPost(17L, "제목4", "내용4", true, board1, parent1);
        post5 = Creator.createPost(18L, "제목5", "내용5", true, board2, parent1);
        post6 = Creator.createPost(19L, "제목6", "내용6", true, board2, parent1);
        post7 = Creator.createPost(20L, "제목7", "내용7", true, board2, parent1);
        post8 = Creator.createPost(21L, "제목8", "내용8", true, board3, parent1);
        post9 = Creator.createPost(22L, "제목9", "내용9", true, board3, parent1);
        post10 = Creator.createPost(23L, "제목10", "내용10", true, board3, parent1);
        post11 = Creator.createPost(24L, "제목11", "내용11", true, board4, parent2);
        post12 = Creator.createPost(25L, "제목12", "내용12", true, board5, parent3);
        post13 = Creator.createPost(26L, "제목13", "내용13", true, board5, parent3);

        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);
        multipartFile = new MockMultipartFile(name, name, "image", content);
        multipartFileList.add(multipartFile);
        multipartFileList.add(multipartFile);
    }

    @Test
    public void 게시글_저장_비회원() throws Exception {
        //given
        postRegisterRequest.setAnonymous(true);
        postRegisterRequest.setBoard_id(board1.getId());
        postRegisterRequest.setContent("게시글 저장 내용");
        postRegisterRequest.setTitle("게시글 저장 제목");

        //when
        UserException result = assertThrows(UserException.class,
                () -> postService.savePost(postRegisterRequest, new ArrayList<>(), null));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(UserErrorResult.NOT_VALID_TOKEN);
    }

    @Test
    public void 게시글_저장_유저X() throws Exception {
        //given
        postRegisterRequest.setAnonymous(true);
        postRegisterRequest.setBoard_id(board1.getId());
        postRegisterRequest.setContent("게시글 저장 내용");
        postRegisterRequest.setTitle("게시글 저장 제목");

        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(parent1.getId());

        //when
        UserException result = assertThrows(UserException.class,
                () -> postService.savePost(postRegisterRequest, new ArrayList<>(), parent1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(UserErrorResult.USER_NOT_EXIST);
    }

    @Test
    public void 게시글_저장_게시판X() throws Exception {
        //given
        postRegisterRequest.setAnonymous(true);
        postRegisterRequest.setBoard_id(board1.getId());
        postRegisterRequest.setContent("게시글 저장 내용");
        postRegisterRequest.setTitle("게시글 저장 제목");

        Mockito.doReturn(Optional.of(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(Optional.empty())
                .when(boardRepository)
                .findById(board1.getId());

        //when
        BoardException result = assertThrows(BoardException.class,
                () -> postService.savePost(postRegisterRequest, new ArrayList<>(), parent1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(BoardErrorResult.BOARD_NOT_EXIST);
    }

    @Test
    public void 게시글_저장_학부모가_공지_게시판_접근() throws Exception {
        //given
        postRegisterRequest.setAnonymous(true);
        postRegisterRequest.setBoard_id(board1.getId());
        postRegisterRequest.setContent("게시글 저장 내용");
        postRegisterRequest.setTitle("게시글 저장 제목");

        Mockito.doReturn(Optional.of(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(Optional.of(board1))
                .when(boardRepository)
                .findById(board1.getId());

        //when
        PostException result = assertThrows(PostException.class,
                () -> postService.savePost(postRegisterRequest, new ArrayList<>(), parent1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PostErrorResult.PARENT_NOT_ACCESS_NOTICE);
    }

    @Test
    public void 게시글_저장_성공() throws Exception {
        //given
        postRegisterRequest.setAnonymous(true);
        postRegisterRequest.setBoard_id(board2.getId());
        postRegisterRequest.setContent("게시글 저장 내용");
        postRegisterRequest.setTitle("게시글 저장 제목");

        Mockito.doReturn(Optional.of(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(Optional.of(board2))
                .when(boardRepository)
                .findById(board2.getId());

        Mockito.doReturn(post1)
                .when(postRepository)
                .save(any());

        Mockito.doReturn(null)
                .when(imageService)
                .saveInfoImages(any(), any());

        //when
        Long savedId = postService.savePost(postRegisterRequest, multipartFileList, parent1.getId());
        //then
        assertThat(savedId).isEqualTo(post1.getId());
    }

    @Test
    public void 게시글_삭제_유저X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(parent1.getId());
        //when
        UserException result = assertThrows(UserException.class,
                () -> postService.deleteById(post1.getId(), parent1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(UserErrorResult.USER_NOT_EXIST);
    }

    @Test
    public void 게시글_삭제_게시글X() throws Exception {
        //given
        Mockito.doReturn(Optional.of(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(Optional.empty())
                .when(postRepository)
                .findById(post1.getId());
        //when
        PostException result = assertThrows(PostException.class,
                () -> postService.deleteById(post1.getId(), parent1.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PostErrorResult.POST_NOT_EXIST);

    }

    @Test
    public void 게시글_삭제_권한X() throws Exception {
        //given
        Mockito.doReturn(Optional.of(parent2))
                .when(userRepository)
                .findById(parent2.getId());

        Mockito.doReturn(Optional.of(post1))
                .when(postRepository)
                .findById(post1.getId());
        //when
        PostException result = assertThrows(PostException.class,
                () -> postService.deleteById(post1.getId(), parent2.getId()));

        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PostErrorResult.UNAUTHORIZED_USER_ACCESS);

    }

    @Test
    public void 게시글_삭제_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.of(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        Mockito.doReturn(Optional.of(post1))
                .when(postRepository)
                .findById(post1.getId());
        //when
        Long savedId = postService.deleteById(post1.getId(), parent1.getId());

        //then
        assertThat(savedId)
                .isEqualTo(post1.getId());

    }

    @Test
    public void 게시글_1개_조회() throws Exception {
        //given
        GetPostResponse getPostResponse = new GetPostResponse(post1, new ArrayList<>(), null);

        Mockito.doReturn(Optional.of(post1))
                .when(postRepository)
                .findByIdWithUserAndBoardAndCenter(post1.getId());

        //when
        GetPostResponse resultDTO = postService.findById(post1.getId());

        //then
        assertThat(objectMapper.writeValueAsString(resultDTO))
                .isEqualTo(objectMapper.writeValueAsString(getPostResponse));

    }

    @Test
    public void 모두_센터_통합검색_비회원() throws Exception {
        //given

        //when
        UserException result = assertThrows(UserException.class,
                () -> postService.searchByKeyword("1", null, PageRequest.of(0, 10)));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(UserErrorResult.NOT_VALID_TOKEN);

    }

    @Test
    public void 모두_센터_통합검색_유저X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findById(parent1.getId());
        //when
        UserException result = assertThrows(UserException.class,
                () -> postService.searchByKeyword("1", parent1.getId(), PageRequest.of(0, 10)));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(UserErrorResult.USER_NOT_EXIST);

    }


    
    @Test
    public void 모두_센터_통합검색_성공_학부모() throws Exception {
        //given

        Mockito.doReturn(Optional.of(parent1))
                .when(userRepository)
                .findById(parent1.getId());

        List<GetPostResponsePreview> previewList = Arrays.asList(
                new GetPostResponsePreview(post1),
                new GetPostResponsePreview(post10),
                new GetPostResponsePreview(post11),
                new GetPostResponsePreview(post12),
                new GetPostResponsePreview(post13)
        );
        Slice<GetPostResponsePreview> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);

        List<Child> children = Arrays.asList(child1, child2, child3);
        Mockito.doReturn(children)
                .when(userRepository)
                .findChildren(parent1.getId());

        Set<Long> centerIds = Set.of(center1.getId(), center2.getId(), center3.getId());
        String keyword = "1";
        Mockito.doReturn(previewSlice)
                .when(postRepository)
                .findInCenterByKeyword(centerIds, keyword, PageRequest.of(0, 10));

        //when
        Slice<GetPostResponsePreview> result = postService
                .searchByKeyword(keyword, parent1.getId(), PageRequest.of(0, 10));
        //then

        assertThat(objectMapper.writeValueAsString(previewSlice))
                .isEqualTo(objectMapper.writeValueAsString(result));
    }

    @Test
    public void 모두_센터_통합검색_성공_교사_센터_NULL() throws Exception {
        //given

        Mockito.doReturn(Optional.of(teacher2))
                .when(userRepository)
                .findById(teacher2.getId());

        List<GetPostResponsePreview> previewList = new ArrayList<>();
        Slice<GetPostResponsePreview> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);

        Set<Long> centerIds = new HashSet<>();
        String keyword = "1";
        Mockito.doReturn(previewSlice)
                .when(postRepository)
                .findInCenterByKeyword(centerIds, keyword, PageRequest.of(0, 10));

        //when
        Slice<GetPostResponsePreview> result = postService
                .searchByKeyword(keyword, teacher2.getId(), PageRequest.of(0, 10));
        //then

        assertThat(objectMapper.writeValueAsString(previewSlice))
                .isEqualTo(objectMapper.writeValueAsString(result));
    }

    @Test
    public void 모두_센터_통합검색_성공_교사() throws Exception {
        //given
        teacher2.mappingCenter(center3);

        Mockito.doReturn(Optional.of(teacher2))
                .when(userRepository)
                .findById(teacher2.getId());

        List<GetPostResponsePreview> previewList = Arrays.asList(
                new GetPostResponsePreview(post12),
                new GetPostResponsePreview(post13)
        );

        Slice<GetPostResponsePreview> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);

        Set<Long> centerIds = Set.of(center3.getId());
        String keyword = "1";
        Mockito.doReturn(previewSlice)
                .when(postRepository)
                .findInCenterByKeyword(centerIds, keyword, PageRequest.of(0, 10));

        //when
        Slice<GetPostResponsePreview> result = postService
                .searchByKeyword(keyword, teacher2.getId(), PageRequest.of(0, 10));
        //then

        assertThat(objectMapper.writeValueAsString(previewSlice))
                .isEqualTo(objectMapper.writeValueAsString(result));
    }

    @Test
    public void 센터_내_검색_학부모_권한X() throws Exception {
        //given
        List<Child> children = Arrays.asList(child1, child2, child3);
        Mockito.doReturn(children)
                .when(userRepository)
                .findChildren(parent1.getId());

        //when
        PostException result = assertThrows(PostException.class,
                () -> postService.searchByKeywordAndCenter(
                        999L, "1", Auth.PARENT, parent1.getId(), PageRequest.of(0, 10)));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 센터_내_검색_학부모_성공() throws Exception {
        //given
        List<Child> children = Arrays.asList(child1, child2, child3);
        Mockito.doReturn(children)
                .when(userRepository)
                .findChildren(parent1.getId());

        List<GetPostResponsePreview> previewList = Arrays.asList(
                new GetPostResponsePreview(post1),
                new GetPostResponsePreview(post10),
                new GetPostResponsePreview(post11),
                new GetPostResponsePreview(post12),
                new GetPostResponsePreview(post13)
        );
        Slice<GetPostResponsePreview> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);

        Mockito.doReturn(previewSlice)
                .when(postRepository)
                .findByCenterAndKeyword(center1.getId(), "1", PageRequest.of(0, 10));


        //when
        Slice<GetPostResponsePreview> result = postService.searchByKeywordAndCenter(
                center1.getId(), "1", Auth.PARENT, parent1.getId(), PageRequest.of(0, 10));
        //then
        assertThat(objectMapper.writeValueAsString(result))
                .isEqualTo(objectMapper.writeValueAsString(previewSlice));
    }

    @Test
    public void 센터_내_검색_교사_권한X() throws Exception {
        //given
        Mockito.doReturn(Optional.empty())
                .when(userRepository)
                .findTeacherById(teacher1.getId());

        //when
        PostException result = assertThrows(PostException.class,
                () -> postService.searchByKeywordAndCenter(
                        center1.getId(), "1", Auth.TEACHER, teacher1.getId(), PageRequest.of(0, 10)));
        //then
        assertThat(result.getErrorResult())
                .isEqualTo(PostErrorResult.UNAUTHORIZED_USER_ACCESS);
    }

    @Test
    public void 센터_내_검색_교사_성공() throws Exception {
        //given
        Mockito.doReturn(Optional.of(teacher1))
                .when(userRepository)
                .findTeacherById(teacher1.getId());

        List<GetPostResponsePreview> previewList = Arrays.asList(
                new GetPostResponsePreview(post1)
        );
        Slice<GetPostResponsePreview> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);

        Mockito.doReturn(previewSlice)
                .when(postRepository)
                .findByCenterAndKeyword(center1.getId(), "1", PageRequest.of(0, 10));

        //when
        Slice<GetPostResponsePreview> result = postService.searchByKeywordAndCenter(
                center1.getId(), "1", Auth.TEACHER, teacher1.getId(), PageRequest.of(0, 10));

        //then
        assertThat(objectMapper.writeValueAsString(result))
                .isEqualTo(objectMapper.writeValueAsString(previewSlice));
    }



}