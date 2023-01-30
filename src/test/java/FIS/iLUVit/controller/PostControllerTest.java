package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.PostService;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    PostController postController;

    @Mock
    PostService postService;

    ObjectMapper objectMapper;

    PostRequest postRequest = new PostRequest();

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
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setCustomArgumentResolvers(new LoginUserArgumentResolver("secretKey"),
                        new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(GlobalControllerAdvice.class)
                .build();

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

        postRequest.setTitle("제목1016");
        postRequest.setContent("내용1016");
        postRequest.setBoard_id(board1.getId());
        postRequest.setAnonymous(true);

    }

    public String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    @Test
    public void 게시글_저장_비회원() throws Exception {
        //given
        byte[] request = objectMapper.writeValueAsBytes(postRequest);

        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);

        MockMultipartFile multipartFile1 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", request);
        List<MultipartFile> fileList = Arrays.asList(multipartFile1, multipartFile2);

        String url = "/user/post";
        UserErrorResult error = UserErrorResult.NOT_VALID_TOKEN;

        Mockito.doThrow(new UserException(error))
                .when(postService)
                .savePost(postRequest, fileList, null);
        //when


        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(url)
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .file(jsonFile)

        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 게시글_저장_유저X() throws Exception {
        //given
        byte[] request = objectMapper.writeValueAsBytes(postRequest);

        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);

        MockMultipartFile multipartFile1 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", request);
        List<MultipartFile> fileList = Arrays.asList(multipartFile1, multipartFile2);

        String url = "/user/post";
        UserErrorResult error = UserErrorResult.USER_NOT_EXIST;

        Mockito.doThrow(new UserException(error))
                .when(postService)
                .savePost(postRequest, fileList, parent1.getId());
        //when


        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(url)
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .file(jsonFile)
                        .header("Authorization", createJwtToken(parent1))

        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_저장_게시판X() throws Exception {
        byte[] request = objectMapper.writeValueAsBytes(postRequest);

        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);

        MockMultipartFile multipartFile1 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", request);

        List<MultipartFile> fileList = Arrays.asList(multipartFile1, multipartFile2);

        String url = "/user/post";
        BoardErrorResult error = BoardErrorResult.BOARD_NOT_EXIST;

        Mockito.doThrow(new BoardException(error))
                .when(postService)
                .savePost(postRequest, fileList, parent1.getId());
        //when


        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(url)
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .file(jsonFile)
                        .header("Authorization", createJwtToken(parent1))

        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_저장_공지게시판에_학부모가_작성시도() throws Exception {
        byte[] request = objectMapper.writeValueAsBytes(postRequest);

        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);

        MockMultipartFile multipartFile1 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", request);

        List<MultipartFile> fileList = Arrays.asList(multipartFile1, multipartFile2);

        String url = "/user/post";
        PostErrorResult error = PostErrorResult.PARENT_NOT_ACCESS_NOTICE;

        Mockito.doThrow(new PostException(error))
                .when(postService)
                .savePost(postRequest, fileList, parent1.getId());
        //when


        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(url)
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .file(jsonFile)
                        .header("Authorization", createJwtToken(parent1))

        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_저장_성공() throws Exception {
        byte[] request = objectMapper.writeValueAsBytes(postRequest);

        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);

        MockMultipartFile multipartFile1 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", request);

        List<MultipartFile> fileList = Arrays.asList(multipartFile1, multipartFile2);

        String url = "/user/post";

        Mockito.doReturn(post1.getId())
                .when(postService)
                .savePost(postRequest, fileList, parent1.getId());
        //when


        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(url)
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .file(jsonFile)
                        .header("Authorization", createJwtToken(parent1))

        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        post1.getId()
                )));
    }

    @Test
    public void 게시글_저장_성공_APP용() throws Exception {
        byte[] request = objectMapper.writeValueAsBytes(postRequest);

        String name = "162693895955046828.png";
        Path path = Paths.get(new File("").getAbsolutePath() + '/' + name);
        byte[] content = Files.readAllBytes(path);

        MockMultipartFile multipartFile1 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", name, "image", content);
        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", request);

        List<MultipartFile> fileList = Arrays.asList(multipartFile1, multipartFile2);

        String url = "/user/post/react-native";

        Mockito.doReturn(post1.getId())
                .when(postService)
                .savePost(postRequest, fileList, parent1.getId());
        //when


        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.multipart(url)
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .flashAttr("request", postRequest)
                        .header("Authorization", createJwtToken(parent1))

        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        post1.getId()
                )));
    }

    @Test
    public void 게시글_삭제_비회원() throws Exception {
        //given

        String url = "/user/post/{post_id}";
        UserErrorResult error = UserErrorResult.NOT_VALID_TOKEN;

        Mockito.doThrow(new UserException(error))
                .when(postService)
                .deleteById(post1.getId(), null);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, post1.getId())
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_삭제_유저X() throws Exception {
        //given

        String url = "/user/post/{post_id}";
        UserErrorResult error = UserErrorResult.USER_NOT_EXIST;

        Mockito.doThrow(new UserException(error))
                .when(postService)
                .deleteById(post1.getId(), parent1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, post1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_삭제_게시글X() throws Exception {
        //given

        String url = "/user/post/{post_id}";
        PostErrorResult error = PostErrorResult.POST_NOT_EXIST;

        Mockito.doThrow(new PostException(error))
                .when(postService)
                .deleteById(post1.getId(), parent1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, post1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_삭제_권한X() throws Exception {
        //given

        String url = "/user/post/{post_id}";
        PostErrorResult error = PostErrorResult.UNAUTHORIZED_USER_ACCESS;

        Mockito.doThrow(new PostException(error))
                .when(postService)
                .deleteById(post1.getId(), parent1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, post1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_삭제_성공() throws Exception {
        //given
        String url = "/user/post/{post_id}";
        Mockito.doReturn(post1.getId())
                .when(postService)
                .deleteById(post1.getId(), parent1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, post1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        post1.getId()
                )));
    }

    @Test
    public void 게시글_1개_조회_게시글X() throws Exception {
        //given
        String url = "/post/{post_id}";
        PostErrorResult error = PostErrorResult.POST_NOT_EXIST;

        Mockito.doThrow(new PostException(error))
                .when(postService)
                .findById(null, post1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url, post1.getId())
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 게시글_1개_조회_성공() throws Exception {
        //given
        PostResponse response = new PostResponse(post1, new ArrayList<>(), null, null);

        String url = "/post/{post_id}";

        Mockito.doReturn(response)
                .when(postService)
                .findById(null, post1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url, post1.getId())
        );
        //then
        System.out.println("response = " + response);
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        response
                )));
    }

    @Test
    public void 게시글_제목_내용_검색_비회원() throws Exception {
        //given
        String url = "/user/post/all/search";

        UserErrorResult error = UserErrorResult.NOT_VALID_TOKEN;
        Mockito.doThrow(new UserException(error))
                .when(postService)
                .searchByKeyword("1", null, PageRequest.of(0, 10));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("input", "1")
                        .param("page", "0")
                        .param("size", "10")
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_제목_내용_검색_유저X() throws Exception {
        //given
        String url = "/user/post/all/search";

        UserErrorResult error = UserErrorResult.USER_NOT_EXIST;
        Mockito.doThrow(new UserException(error))
                .when(postService)
                .searchByKeyword("1", parent1.getId(), PageRequest.of(0, 10));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("input", "1")
                        .param("page", "0")
                        .param("size", "10")
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 게시글_제목_내용_검색_성공() throws Exception {
        //given
        String url = "/user/post/all/search";

        List<PostPreviewDto> previewList = Arrays.asList(
                new PostPreviewDto(post1),
                new PostPreviewDto(post10),
                new PostPreviewDto(post11),
                new PostPreviewDto(post12),
                new PostPreviewDto(post13)
        );
        Slice<PostPreviewDto> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);

        Mockito.doReturn(previewSlice)
                .when(postService)
                .searchByKeyword("1", parent1.getId(), PageRequest.of(0, 10));

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("input", "1")
                        .param("page", "0")
                        .param("size", "10")
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        previewSlice
                )));
    }

    @Test
    public void 센터에서_게시글_제목_내용_검색_해당_센터에_권한없음() throws Exception {
        //given
        String url = "/post/search/inCenter";
        PostErrorResult error = PostErrorResult.UNAUTHORIZED_USER_ACCESS;

        Mockito.doThrow(new PostException(error))
                .when(postService)
                .searchByKeywordAndCenter(
                        center1.getId(), "1", Auth.PARENT,
                        parent1.getId(), PageRequest.of(0, 10)
                );
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("center_id", center1.getId().toString())
                        .param("input", "1")
                        .param("auth", Auth.PARENT.toString())
                        .param("page", "0")
                        .param("size", "10")
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 보드에서_게시글_제목_내용_검색() throws Exception {
        //given
        String url = "/post/search/inBoard";

        List<PostPreviewDto> previewList = Arrays.asList(
                new PostPreviewDto(post1),
                new PostPreviewDto(post2),
                new PostPreviewDto(post3),
                new PostPreviewDto(post4)
        );

        Slice<PostPreviewDto> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);

        Mockito.doReturn(previewSlice)
                .when(postService)
                .searchByKeywordAndBoard(board1.getId(), "제목", PageRequest.of(0, 10));

        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("board_id", board1.getId().toString())
                        .param("input", "제목")
                        .param("page", "0")
                        .param("size", "10")
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        previewSlice
                )));
    }

    @Test
    public void HOT_게시판_글_목록_조회() throws Exception {
        //given
        String url = "/post/search/hotBoard";

        List<PostPreviewDto> previewList = Arrays.asList(
                new PostPreviewDto(post1),
                new PostPreviewDto(post2),
                new PostPreviewDto(post3),
                new PostPreviewDto(post4)
        );

        Slice<PostPreviewDto> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);

        Mockito.doReturn(previewSlice)
                .when(postService)
                .findByHeartCnt(center1.getId(), PageRequest.of(0, 10));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("center_id", center1.getId().toString())
                        .param("page", "0")
                        .param("size", "10")
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        previewSlice
                )));

    }

    @Test
    public void 내가_쓴_글_리스트() throws Exception {
        //given
        String url = "/user/post/mypage";

        List<PostPreviewDto> previewList = Arrays.asList(
                new PostPreviewDto(post1),
                new PostPreviewDto(post2),
                new PostPreviewDto(post3),
                new PostPreviewDto(post4)
        );

        Slice<PostPreviewDto> previewSlice = new SliceImpl<>
                (previewList, PageRequest.of(0, 10), false);


        Mockito.doReturn(previewSlice)
                .when(postService)
                .searchByUser(parent1.getId(), PageRequest.of(0, 10));
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("page", "0")
                        .param("size", "10")
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        previewSlice
                )));
    }

    @Test
    public void 모두의_이야기_글_리스트_불러오기_비회원() throws Exception {
        //given
        String url = "/post/modu-main";
        Board board6 = CreateTest.createBoard(99L, "자유게시판", BoardKind.NORMAL, null, true);
        Board board7 = CreateTest.createBoard(100L, "정보게시판", BoardKind.NORMAL, null, true);
        Board board8 = CreateTest.createBoard(101L, "홍보게시판", BoardKind.NORMAL, null, true);

        Post post90 = Creator.createPost(102L, "제목90", "내용90", true, board6, parent1);
        Post post91 = Creator.createPost(103L, "제목91", "내용91", true, board6, parent1);
        Post post92 = Creator.createPost(104L, "제목92", "내용92", true, board7, parent1);
        Post post93 = Creator.createPost(105L, "제목93", "내용93", true, board8, parent1);
        Post post94 = Creator.createPost(106L, "제목94", "내용94", true, board8, parent1);
        Post post95 = Creator.createPost(107L, "제목95", "내용95", true, board8, parent1);
        Post post96 = Creator.createPost(108L, "제목96", "내용96", true, board8, parent1);

        BoardPreviewDto boardPreview1 = new BoardPreviewDto(board6.getId(), board6.getName(),
                Arrays.asList(new BoardPreviewDto.PostInfo(post90), new BoardPreviewDto.PostInfo(post91)),
                board6.getBoardKind());

        BoardPreviewDto boardPreview2 = new BoardPreviewDto(board7.getId(), board7.getName(),
                Arrays.asList(new BoardPreviewDto.PostInfo(post92)),
                board6.getBoardKind());

        BoardPreviewDto boardPreview3 = new BoardPreviewDto(board8.getId(), board8.getName(),
                Arrays.asList(new BoardPreviewDto.PostInfo(post93), new BoardPreviewDto.PostInfo(post94)),
                board6.getBoardKind());


        BoardPreviewDto hots = new BoardPreviewDto(null, "HOT 게시판",
                Arrays.asList(new BoardPreviewDto.PostInfo(post95), new BoardPreviewDto.PostInfo(post96)),
                BoardKind.NORMAL);

        List<BoardPreviewDto> boardPreviews = Arrays.asList(hots, boardPreview1, boardPreview2, boardPreview3);

        Mockito.doReturn(boardPreviews)
                .when(postService)
                .searchMainPreview(null);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
//                        .header("Authorization", createJwtToken(parent1))
                        .param("page", "0")
                        .param("size", "10")
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        boardPreviews
                )));
    }

    @Test
    public void 모두의_이야기_글_리스트_불러오기_회원() throws Exception {
        //given
        String url = "/post/modu-main";
        Board board6 = CreateTest.createBoard(99L, "자유게시판", BoardKind.NORMAL, null, true);
        Board board7 = CreateTest.createBoard(100L, "정보게시판", BoardKind.NORMAL, null, true);
        Board board8 = CreateTest.createBoard(101L, "홍보게시판", BoardKind.NORMAL, null, true);

        Post post90 = Creator.createPost(102L, "제목90", "내용90", true, board6, parent1);
        Post post91 = Creator.createPost(103L, "제목91", "내용91", true, board6, parent1);
        Post post92 = Creator.createPost(104L, "제목92", "내용92", true, board7, parent1);
        Post post93 = Creator.createPost(105L, "제목93", "내용93", true, board8, parent1);
        Post post94 = Creator.createPost(106L, "제목94", "내용94", true, board8, parent1);
        Post post95 = Creator.createPost(107L, "제목95", "내용95", true, board8, parent1);
        Post post96 = Creator.createPost(108L, "제목96", "내용96", true, board8, parent1);

        BoardPreviewDto boardPreview1 = new BoardPreviewDto(board6.getId(), board6.getName(),
                Arrays.asList(new BoardPreviewDto.PostInfo(post90), new BoardPreviewDto.PostInfo(post91)),
                board6.getBoardKind());

        BoardPreviewDto boardPreview2 = new BoardPreviewDto(board7.getId(), board7.getName(),
                Arrays.asList(new BoardPreviewDto.PostInfo(post92)),
                board6.getBoardKind());

        BoardPreviewDto boardPreview3 = new BoardPreviewDto(board8.getId(), board8.getName(),
                Arrays.asList(new BoardPreviewDto.PostInfo(post93), new BoardPreviewDto.PostInfo(post94)),
                board6.getBoardKind());


        BoardPreviewDto hots = new BoardPreviewDto(null, "HOT 게시판",
                Arrays.asList(new BoardPreviewDto.PostInfo(post95), new BoardPreviewDto.PostInfo(post96)),
                BoardKind.NORMAL);

        List<BoardPreviewDto> boardPreviews = Arrays.asList(hots, boardPreview1, boardPreview2, boardPreview3);

        Mockito.doReturn(boardPreviews)
                .when(postService)
                .searchMainPreview(parent1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("page", "0")
                        .param("size", "10")
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        boardPreviews
                )));
    }

    @Test
    public void 유치원별_이야기_글_리스트_불러오기_비회원() throws Exception {
        //given
        String url = "/user/post/center-main";
        UserErrorResult error = UserErrorResult.NOT_VALID_TOKEN;

        Mockito.doThrow(new UserException(error))
                .when(postService)
                .searchCenterMainPreview(null, center1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("center_id", center1.getId().toString())
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 유치원별_이야기_글_리스트_불러오기_유저X() throws Exception {
        //given
        String url = "/user/post/center-main";
        UserErrorResult error = UserErrorResult.USER_NOT_EXIST;

        Mockito.doThrow(new UserException(error))
                .when(postService)
                .searchCenterMainPreview(parent1.getId(), center1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("center_id", center1.getId().toString())
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 유치원별_이야기_글_리스트_불러오기_권한X() throws Exception {
        //given
        String url = "/user/post/center-main";
        PostErrorResult error = PostErrorResult.UNAUTHORIZED_USER_ACCESS;

        Mockito.doThrow(new PostException(error))
                .when(postService)
                .searchCenterMainPreview(parent1.getId(), center1.getId());

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("center_id", center1.getId().toString())
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 유치원별_이야기_글_리스트_불러오기_성공() throws Exception {
        //given
        String url = "/user/post/center-main";

        List<BoardPreviewDto.PostInfo> postInfoList1 = Arrays.asList(
                new BoardPreviewDto.PostInfo(post1),
                new BoardPreviewDto.PostInfo(post3),
                new BoardPreviewDto.PostInfo(post5),
                new BoardPreviewDto.PostInfo(post7)
        );

        BoardPreviewDto boardPreview1 = new BoardPreviewDto(null, "HOT 게시판", postInfoList1, BoardKind.NORMAL);

        List<BoardPreviewDto.PostInfo> postInfoList2 = Arrays.asList(
                new BoardPreviewDto.PostInfo(post1),
                new BoardPreviewDto.PostInfo(post3)
        );

        BoardPreviewDto boardPreview2 = new BoardPreviewDto(board1.getId(), board1.getName(), postInfoList2, board1.getBoardKind());

        List<BoardPreviewDto.PostInfo> postInfoList3 = Arrays.asList(
                new BoardPreviewDto.PostInfo(post5),
                new BoardPreviewDto.PostInfo(post7)
        );

        BoardPreviewDto boardPreview3 = new BoardPreviewDto(board2.getId(), board2.getName(), postInfoList3, board2.getBoardKind());

        List<BoardPreviewDto.PostInfo> postInfoList4 = Arrays.asList(
                new BoardPreviewDto.PostInfo(post9),
                new BoardPreviewDto.PostInfo(post10)
        );

        BoardPreviewDto boardPreview4 = new BoardPreviewDto(board3.getId(), board3.getName(), postInfoList4, board3.getBoardKind());

        List<BoardPreviewDto> boardPreviewList = Arrays.asList(
                boardPreview1, boardPreview2, boardPreview3, boardPreview4);

        Mockito.doReturn(boardPreviewList)
                .when(postService)
                .searchCenterMainPreview(parent1.getId(), center1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", createJwtToken(parent1))
                        .param("center_id", center1.getId().toString())
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                       boardPreviewList
                )));
    }

    @Test
    public void 장터글_끌어올리기_비회원() throws Exception {
        //given
        String url = "/post/update/{post_id}";
        UserErrorResult error = UserErrorResult.NOT_VALID_TOKEN;

        Mockito.doThrow(new UserException(error))
                .when(postService)
                .updateDate(null, post1.getId());
        //when

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put(url, post1.getId())
        );
        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));

    }

    @Test
    public void 장터글_끌어올리기_게시글X() throws Exception {
        //given
        String url = "/post/update/{post_id}";
        PostErrorResult error = PostErrorResult.POST_NOT_EXIST;

        Mockito.doThrow(new PostException(error))
                .when(postService)
                .updateDate(parent1.getId(), post1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put(url, post1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isIAmATeapot())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 장터글_끌어올리기_권한X() throws Exception {
        //given
        String url = "/post/update/{post_id}";
        PostErrorResult error = PostErrorResult.UNAUTHORIZED_USER_ACCESS;

        Mockito.doThrow(new PostException(error))
                .when(postService)
                .updateDate(parent1.getId(), post1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put(url, post1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ErrorResponse(error.getHttpStatus(), error.getMessage())
                )));
    }

    @Test
    public void 장터글_끌어올리기_성공() throws Exception {
        //given
        String url = "/post/update/{post_id}";

        Mockito.doNothing()
                .when(postService)
                .updateDate(parent1.getId(), post1.getId());
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put(url, post1.getId())
                        .header("Authorization", createJwtToken(parent1))
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk());
    }
}