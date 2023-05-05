package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.dto.post.PostPreviewDto;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import FIS.iLUVit.domain.iluvit.enumtype.BoardKind;
import FIS.iLUVit.repository.iluvit.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class PostRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    PostRepository postRepository;

    ObjectMapper objectMapper;

    Center center1;
    Center center2;
    Center center3;

    Long centerId1;
    Long centerId2;
    Long centerId3;

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
    public void init() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        center1 = Creator.createCenter("팡팡유치원", true, true, null);
        center2 = Creator.createCenter("가산유치원", true, true, null);
        center3 = Creator.createCenter("디지털유치원", true, true, null);

        parent1 = Parent.builder()
                .name("ParentA")
                .auth(Auth.PARENT)
                .build();
        parent1.getChildren().add(child1);
        parent1.getChildren().add(child2);
        parent1.getChildren().add(child3);

        child1 = Child.createChild("childA", null, null, parent1);
        child2 = Child.createChild("childB", null, null, parent1);
        child3 = Child.createChild("childC", null, null, parent1);
        child1.mappingCenter(center1);

        parent2 = Parent.builder()
                .name("ParentB")
                .auth(Auth.PARENT)
                .build();
        parent3 = Parent.builder()
                .name("ParentC")
                .auth(Auth.PARENT)
                .build();
        teacher1 = Teacher.builder()
                .center(center1)
                .approval(Approval.WAITING)
                .name("TeacherA")
                .auth(Auth.TEACHER)
                .build();
        teacher2 = Teacher.builder()
                .center(center1)
                .approval(Approval.ACCEPT)
                .name("TeacherB")
                .auth(Auth.TEACHER)
                .build();

        board1 = Board.createBoard("홍보게시판", BoardKind.NORMAL, center1, false);
        board2 = Board.createBoard("자유게시판", BoardKind.NORMAL, center1, true);
        board3 = Board.createBoard("정보게시판", BoardKind.NORMAL, center1, true);
        board4 = Board.createBoard("장터게시판", BoardKind.NORMAL, center2, true);
        board5 = Board.createBoard("영상게시판", BoardKind.NORMAL, center3, true);

        post1 = Creator.createPost("제목1", "내용1", true, board1, parent1);
        post2 = Creator.createPost("제목2", "내용2", true, board1, parent1);
        post3 = Creator.createPost("제목3", "내용3", true, board1, parent1);
        post4 = Creator.createPost("제목4", "내용4", true, board1, parent1);
        post5 = Creator.createPost("제목5", "내용5", true, board2, parent1);
        post6 = Creator.createPost("제목6", "내용6", true, board2, parent1);
        post7 = Creator.createPost("제목7", "내용7", true, board2, parent1);
        post8 = Creator.createPost("제목8", "내용8", true, board3, parent1);
        post9 = Creator.createPost("제목9", "내용9", true, board3, parent1);
        post10 = Creator.createPost("제목10", "내용10", true, board3, parent1);
        post11 = Creator.createPost("제목11", "내용11", true, board4, parent2);
        post12 = Creator.createPost("제목12", "내용12", true, board5, parent3);
        post13 = Creator.createPost("제목13", "내용13", true, board5, parent3);


        centerId1 = em.persist(center1).getId();
        centerId2 = em.persist(center2).getId();
        centerId3 = em.persist(center3).getId();
        em.persist(board1);
        em.persist(board2);
        em.persist(board3);
        em.persist(board4);
        em.persist(board5);
        em.persist(post1);
        em.persist(post2);
        em.persist(post3);
        em.persist(post4);
        em.persist(post5);
        em.persist(post6);
        em.persist(post7);
        em.persist(post8);
        em.persist(post9);
        em.persist(post10);
        em.persist(post11);
        em.persist(post12);
        em.persist(post13);
        em.persist(parent1);
        em.persist(parent2);
        em.persist(parent3);
        em.persist(child1);
        em.persist(child2);
        em.persist(child3);
        em.persist(teacher1);
        em.persist(teacher2);
        em.flush();
        em.clear();
    }

    @Test
    public void 게시글_아이디로_찾기_페치조인() throws Exception {
        //given

        //when
        Post findPost = postRepository.findByIdWithUserAndBoardAndCenter(post1.getId())
                .orElse(null);

        //then
        assertThat(findPost.getTitle())
                .isEqualTo("제목1");
        assertThat(findPost.getContent())
                .isEqualTo("내용1");
        assertThat(findPost.getBoard().getId())
                .isEqualTo(board1.getId());
        assertThat(findPost.getAnonymous())
                .isEqualTo(true);
        assertThat(findPost.getUser())
                .isEqualTo(parent1);
        assertThat(findPost.getBoard().getCenter().getId())
                .isEqualTo(center1.getId());
    }

    @Test
    public void 유저아이디로_게시글_찾기() throws Exception {
        //given

        //when
        Slice<Post> postSlice = postRepository.findByUser(parent1.getId(), PageRequest.of(0, 10));
        //then
        List<Post> content = postSlice.getContent();

        assertThat(content.size())
                .isEqualTo(10);
    }

    @Test
    public void 게시판_별_최신글_TOP_4개_조회() throws Exception {
        //given

        List<Long> boardIds = Arrays.asList(board1.getId(), board2.getId(), board3.getId());
        //when
        List<Post> top4List = postRepository.findTop3_H2(boardIds);

        Map<Board, List<Post>> postList = top4List.stream()
                .collect(Collectors.groupingBy(p -> p.getBoard()));

        //then
        for (Board board : postList.keySet()) {
            System.out.println("board.getName() = " + board.getName());
            List<String> titles = postList.get(board)
                    .stream()
                    .map(Post::getTitle)
                    .collect(Collectors.toList());
            String result = objectMapper.writeValueAsString(titles);
            System.out.println("result = " + result);
        }
    }

    @Test
    public void 센터_내_좋아요_수_n개_이상_게시글_TOP_4개_조회() throws Exception {
        //given
        int n = 1;
        Post post11 = Creator.createPost("제목11", "내용11", true, board3, parent1);
        Post post12 = Creator.createPost("제목12", "내용12", true, board3, parent1);

        PostHeart postHeart1 = new PostHeart(parent1, post11);
        PostHeart postHeart2 = new PostHeart(parent1, post12);

        em.persist(post11);
        em.persist(post12);
        em.persist(postHeart1);
        em.persist(postHeart2);
        em.flush();
        em.clear();
        //when
        List<Post> postList = postRepository
                .findTop3ByHeartCntWithCenter(n, center1.getId(), PageRequest.of(0, 4));
        //then
        assertThat(postList.size())
                .isEqualTo(2);
        assertThat(postList)
                .extracting("title")
                .containsOnly("제목11", "제목12");

    }

    @Test
    public void 모두의_이야기_내_좋아요_수_n개_이상_게시글_TOP_4개_조회() throws Exception {
        //given
        int n = 1;
        Board board4 = Board.createBoard("정보게시판", BoardKind.NORMAL, null, true);

        Post post11 = Creator.createPost("제목11", "내용11", true, board4, parent1);
        Post post12 = Creator.createPost("제목12", "내용12", true, board4, parent1);

        PostHeart postHeart1 = new PostHeart(parent1, post11);
        PostHeart postHeart2 = new PostHeart(parent1, post12);

        em.persist(board4);
        em.persist(post11);
        em.persist(post12);
        em.persist(postHeart1);
        em.persist(postHeart2);
        em.flush();
        em.clear();
        //when
        List<Post> postList = postRepository
                .findTop3ByHeartCnt(n, PageRequest.of(0, 4));
        //then
        assertThat(postList.size())
                .isEqualTo(2);
        assertThat(postList)
                .extracting("title")
                .containsOnly("제목11", "제목12");

    }

    @Test
    public void 센터_내_키워드_검색() throws Exception {
        //given

        //when
        Slice<PostPreviewDto> dtoSlice = postRepository
                .findByCenterAndKeyword(center1.getId(), "1", PageRequest.of(0, 10));
        //then
        List<PostPreviewDto> result = dtoSlice.getContent();
        for (PostPreviewDto gprp : result) {
            System.out.println("gprp = " + gprp);
        }
        assertThat(result)
                .extracting("title")
                .containsOnly("제목1", "제목10");
    }

    @Test
    public void 게시판_내_키워드_검색() throws Exception {
        //given

        //when
        Slice<PostPreviewDto> dtoSlice = postRepository
                .findByBoardAndKeyword(board1.getId(), "제목", PageRequest.of(0, 10));
        //then
        List<PostPreviewDto> result = dtoSlice.getContent();
        for (PostPreviewDto gprp : result) {
            System.out.println("gprp = " + gprp);
        }
        assertThat(result)
                .extracting("title")
                .containsOnly("제목1", "제목2", "제목3", "제목4");
    }

    @Test
    public void 모두의_이야기_내_핫_게시글_조회() throws Exception {
        //given
        int n = 1;
        Board board4 = Board.createBoard("정보게시판", BoardKind.NORMAL, null, true);

        Post post11 = Creator.createPost("제목11", "내용11", true, board4, parent1);
        Post post12 = Creator.createPost("제목12", "내용12", true, board4, parent1);

        PostHeart postHeart1 = new PostHeart(parent1, post11);
        PostHeart postHeart2 = new PostHeart(parent1, post12);

        em.persist(board4);
        em.persist(post11);
        em.persist(post12);
        em.persist(postHeart1);
        em.persist(postHeart2);
        em.flush();
        em.clear();

        //when
        Slice<PostPreviewDto> hotPosts = postRepository
                .findHotPosts(null, n, PageRequest.of(0, 10));
        //then
        List<PostPreviewDto> result = hotPosts.getContent();
        for (PostPreviewDto gprp : result) {
            System.out.println("gprp = " + gprp);
        }
        assertThat(result)
                .extracting("title")
                .containsOnly("제목11", "제목12");
    }

    @Test
    public void 센터_내_핫_게시글_조회() throws Exception {
        //given
        int n = 1;
        Post post11 = Creator.createPost("제목11", "내용11", true, board3, parent1);
        Post post12 = Creator.createPost("제목12", "내용12", true, board3, parent1);

        PostHeart postHeart1 = new PostHeart(parent1, post11);
        PostHeart postHeart2 = new PostHeart(parent1, post12);

        em.persist(post11);
        em.persist(post12);
        em.persist(postHeart1);
        em.persist(postHeart2);
        em.flush();
        em.clear();
        //when
        Slice<PostPreviewDto> hotPosts = postRepository
                .findHotPosts(center1.getId(), n, PageRequest.of(0, 10));
        //then
        List<PostPreviewDto> result = hotPosts.getContent();
        for (PostPreviewDto gprp : result) {
            System.out.println("gprp = " + gprp);
        }
        assertThat(result)
                .extracting("title")
                .containsOnly("제목11", "제목12");
    }

    @Test
    public void 센터_여러개와_모두의_이야기_키워드_검색() throws Exception {
        //given

        Set<Long> centerIds = Set.of(centerId1, centerId2, centerId3);
        //when
        Slice<PostPreviewDto> postSlice = postRepository
                .findInCenterByKeyword(centerIds, "1", PageRequest.of(0, 10));
        //then
        List<PostPreviewDto> result = postSlice.getContent();
        for (PostPreviewDto gprp : result) {
            System.out.println("gprp = " + gprp);
        }
        assertThat(result)
                .extracting("title")
                .containsOnly("제목1", "제목10", "제목11", "제목12", "제목13");

    }

}