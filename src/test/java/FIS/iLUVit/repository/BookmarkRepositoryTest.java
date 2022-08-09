package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static FIS.iLUVit.service.createmethod.CreateTest.*;
import static FIS.iLUVit.service.createmethod.CreateTest.createCenter;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class BookmarkRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private BoardRepository boardRepository;

    Board board1;
    Board board2;
    Board board3;
    Board board4;

    Center center1;
    Center center2;

    Bookmark bookmark1;
    Bookmark bookmark2;
    Bookmark bookmark3;
    Bookmark bookmark4;
    Bookmark bookmark5;
    Bookmark bookmark6;

    Parent parent1;
    Teacher teacher1;
    Teacher director1;
    Teacher director2;

    Child child1;

    Post post1;
    Post post2;
    Post post3;
    Post post4;
    Post post5;
    Post post6;

    @BeforeEach
    public void init() {
        parent1 = Parent.builder()
                .auth(Auth.PARENT)
                .build();

        center1 = createCenter("떡잎유치원");
        center2 = createCenter("팡팡어린이집");

        teacher1 = Teacher.builder()
                .auth(Auth.TEACHER)
                .center(center1)
                .build();

        director1 = Teacher.builder()
                .auth(Auth.DIRECTOR)
                .center(null)
                .build();

        director2 = Teacher.builder()
                .auth(Auth.DIRECTOR)
                .center(center2)
                .build();

        child1 = Child.builder()
                .name("어린이")
                .build();

        board1 = createBoard("자유게시판", BoardKind.NORMAL, null, true);
        board2 = createBoard("맛집게시판", BoardKind.NORMAL, null, true);
        board3 = createBoard("정보게시판", BoardKind.NORMAL, center1, true);
        board4 = createBoard("공지게시판", BoardKind.NORMAL, center1, true);

        bookmark1 = createBookmark(board1, parent1);
        bookmark2 = createBookmark(board2, parent1);
        bookmark3 = createBookmark(board3, parent1);
        bookmark4 = createBookmark(board4, parent1);
        bookmark5 = createBookmark(board1, teacher1);
        bookmark6 = createBookmark(board3, teacher1);


        post1 = Creator.createPost("제목1", "내용1", true, 0, 0, 0, 0, board1, parent1);
        post2 = Creator.createPost("제목2", "내용2", true, 0, 0, 0, 0, board2, parent1);
        post3 = Creator.createPost("제목3", "내용3", true, 0, 0, 0, 0, board3, parent1);
        post4 = Creator.createPost("제목4", "내용4", true, 0, 0, 0, 0, board4, parent1);
        post5 = Creator.createPost("제목5", "내용5", true, 0, 0, 0, 0, board1, parent1);
        post6 = Creator.createPost("제목6", "내용6", true, 0, 0, 0, 0, board2, parent1);
    }

    @Test
    public void 모두의_이야기_북마크_조회() throws Exception {
        //given
        em.persist(board1);
        em.persist(board2);
        em.persist(parent1);
        em.persist(bookmark1);
        em.persist(bookmark2);
        em.flush();
        em.clear();
        //when
        List<Bookmark> bookmarkList = bookmarkRepository.findBoardByUser(parent1.getId());
        Board findBoard1 = boardRepository.findById(board1.getId()).get();
        Board findBoard2 = boardRepository.findById(board2.getId()).get();
        //then
        assertThat(bookmarkList).extracting("board")
                .contains(findBoard1, findBoard2);
    }

    @Test
    public void 센터의_이야기_북마크_조회() throws Exception {
        //given
        em.persist(center1);
        em.persist(board3);
        em.persist(parent1);
        em.persist(bookmark3);
        em.flush();
        em.clear();
        //when
        List<Bookmark> bookmarkList = bookmarkRepository.findBoardByUserAndCenter(parent1.getId(), center1.getId());
        Board findBoard1 = boardRepository.findById(board3.getId()).get();
        //then
        assertThat(bookmarkList).extracting("board")
                .contains(findBoard1);
    }

    @Test
    public void 북마크별_최신_게시글_하나씩_조회() throws Exception {
        //given
        em.persist(center1);
        em.persist(board1);
        em.persist(board2);
        em.persist(board3);
        em.persist(board4);
        Parent saved1 = em.persist(parent1);
        em.persist(bookmark1);
        em.persist(bookmark2);
        em.persist(bookmark3);
        em.persist(bookmark4);
        em.persist(post1);
        em.persist(post2);
        em.persist(post3);
        em.persist(post4);
        em.persist(post5);
        em.persist(post6);
        em.flush();
        em.clear();
        //when
        List<Post> postList = bookmarkRepository.findPostByBoard(saved1.getId());
        //then
        List<Board> boardList = postList.stream()
                .map(Post::getBoard)
                .collect(Collectors.toList());

        List<Center> centerList = boardList.stream()
                .filter(b -> b.getCenter() != null)
                .map(Board::getCenter)
                .collect(Collectors.toList());

        assertThat(postList).extracting("title")
                .containsOnly("제목3", "제목4", "제목5", "제목6");

        assertThat(boardList).extracting("name")
                .containsOnly("정보게시판", "공지게시판", "자유게시판", "맛집게시판");

        assertThat(centerList).extracting("name")
                .containsOnly("떡잎유치원");
    }

    @Test
    public void 북마크_삭제_게시판과_유저로() throws Exception {
        //given
        em.persist(center1);
        Board saved1 = em.persist(board1);
        Board saved2 = em.persist(board2);
        Board saved3 = em.persist(board3);
        Board saved4 = em.persist(board4);
        Parent savedParent = em.persist(parent1);
        em.persist(bookmark1);
        em.persist(bookmark2);
        em.persist(bookmark3);
        em.persist(bookmark4);
        em.flush();
        em.clear();

        List<Long> boardIds = Arrays
                .asList(saved1.getId(), saved2.getId(), saved3.getId(), saved4.getId());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        //when
        bookmarkRepository.deleteAllByBoardAndUser(savedParent.getId(), boardIds);

        //then
        List<Bookmark> postDelete = bookmarkRepository.findAll();

        assertThat(bookmarkList.size()).isEqualTo(4);
        assertThat(postDelete).isEmpty();
    }

    @Test
    public void deleteAllByBoardAndUser() {
        // given
        em.persist(center1);
        em.persist(parent1);
        em.persist(board1);
        em.persist(board2);
        em.persist(board3);
        em.persist(board4);
        em.persist(bookmark1);
        em.persist(bookmark2);
        em.persist(bookmark3);
        em.persist(bookmark4);
        em.flush();
        em.clear();
        List<Long> boardIds = List.of(board3.getId(), board4.getId());
        // when
        bookmarkRepository.deleteAllByBoardAndUser(parent1.getId(), boardIds);
        em.flush();
        em.clear();
        // then
        List<Bookmark> result = bookmarkRepository.findByUser(parent1);
        assertThat(result.size()).isEqualTo(2);
        result.forEach(bookmark -> {
            assertThat(bookmark.getBoard().getCenter()).isNull();
        });
    }

    @Test
    public void findByUserWithBoard() {
        // given
        em.persist(center1);
        em.persist(center2);
        em.persist(parent1);
        em.persist(teacher1);
        em.persist(board1);
        em.persist(board2);
        em.persist(board3);
        em.persist(board4);
        em.persist(bookmark1);
        em.persist(bookmark2);
        em.persist(bookmark3);
        em.persist(bookmark4);
        em.persist(bookmark5);
        em.persist(bookmark6);
        em.flush();
        em.clear();
        // when
        List<Bookmark> result = bookmarkRepository.findByUser(parent1);
        // then
        assertThat(result.size()).isEqualTo(4);
        for (Bookmark bookmark : result) {
            assertThat(bookmark.getUser().getId()).isEqualTo(parent1.getId());
        }
    }

    @Test
    public void deleteAllByCenterAndUser() {
        // given
        em.persist(center1);
        em.persist(center2);
        em.persist(parent1);
        em.persist(teacher1);
        em.persist(board1);
        em.persist(board2);
        em.persist(board3);
        em.persist(board4);
        em.persist(bookmark1);
        em.persist(bookmark2);
        em.persist(bookmark3);
        em.persist(bookmark4);
        em.persist(bookmark5);
        em.persist(bookmark6);
        em.flush();
        em.clear();
        // when
        bookmarkRepository.deleteAllByCenterAndUser(parent1.getId(), center1.getId());
        // then
        List<Bookmark> result = bookmarkRepository.findByUserWithBoard(parent1.getId());
        assertThat(result.size()).isEqualTo(2);
        for (Bookmark bookmark : result) {
            if (bookmark.getBoard().getCenter() != null) {
                assertThat(bookmark.getBoard().getCenter().getId()).isNotEqualTo(center1.getId());
            }
        }
    }

}