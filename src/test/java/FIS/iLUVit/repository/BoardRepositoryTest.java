package FIS.iLUVit.repository;

import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.enumtype.BoardKind;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class BoardRepositoryTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }


    @Test
    public void 센터로_게시판_찾기() throws Exception {
        //given
        Board board1 = Board.createBoard("board1", BoardKind.NORMAL, null, false);
        //when
        Board savedBoard1 = boardRepository.save(board1);
        //then
        assertThat(board1).isEqualTo(savedBoard1);
    }
}