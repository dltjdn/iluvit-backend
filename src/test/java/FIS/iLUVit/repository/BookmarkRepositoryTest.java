package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

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

    Parent parent1;
    Teacher teacher1;
    Teacher director1;
    Teacher director2;

    Child child1;

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
        board4 = createBoard("공지게시판", BoardKind.NORMAL, center2, true);

        bookmark1 = createBookmark(board1, parent1);
        bookmark2 = createBookmark(board2, parent1);
        bookmark3 = createBookmark(board3, parent1);
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
        em.flush();
        em.clear();
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

}