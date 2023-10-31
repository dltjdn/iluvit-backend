package FIS.iLUVit.global.init;

import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.board.domain.BoardKind;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class initDB {

    private final InitService initService;

//    @PostConstruct
//    public void init() {
//        initService.dbInit();
//    }


    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        private final BCryptPasswordEncoder encoder;

        public void dbInit() {

            Board board2 = Board.publicOf("자유 게시판", BoardKind.NORMAL, true);
            Board board3 = Board.publicOf("학부모 게시판", BoardKind.NORMAL, true);
            Board board4 = Board.publicOf("선생님 게시판", BoardKind.NORMAL,  true);
            Board board5 = Board.publicOf("원장님 게시판", BoardKind.NORMAL, true);
            Board board6 = Board.publicOf("정보 게시판", BoardKind.NORMAL, true);

            em.persist(board2);
            em.persist(board3);
            em.persist(board4);
            em.persist(board5);
            em.persist(board6);

        }
    }
}
