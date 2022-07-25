package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.PostRegisterRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.service.PostService;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    PostController postController;

    @Mock
    PostService postService;

    ObjectMapper objectMapper;

    List<MultipartFile> multipartFileList = new ArrayList<>();

    PostRegisterRequest postRegisterRequest = new PostRegisterRequest();

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
        MockMultipartFile multipartFile = new MockMultipartFile(name, name, "image", content);
        multipartFileList.add(multipartFile);
        multipartFileList.add(multipartFile);
    }

    @Test
    public void 게시글_저장_비회원() throws Exception {
        //given
        postRegisterRequest.setTitle("제목1016");
        postRegisterRequest.setContent("내용1016");
        postRegisterRequest.setBoard_id(board1.getId());
        postRegisterRequest.setAnonymous(true);

        //when
        String url = "/post";
        UserErrorResult error = UserErrorResult.NOT_VALID_TOKEN;

        //then
    }

    @Test
    public void 게시글_저장_유저X() throws Exception {
        //given

        //when

        //then
    }

    @Test
    public void 게시글_저장_게시판X() throws Exception {
        //given

        //when

        //then
    }

    @Test
    public void 게시글_저장_공지게시판에_학부모가_작성시도() throws Exception {
        //given

        //when

        //then
    }

    @Test
    public void 게시글_저장_성공() throws Exception {
        //given

        //when

        //then
    }
}