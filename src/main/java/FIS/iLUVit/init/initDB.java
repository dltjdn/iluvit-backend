package FIS.iLUVit.init;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDate;

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

            Board board2 = Board.createBoard("자유 게시판", BoardKind.NORMAL, null, true);
            Board board3 = Board.createBoard("영상 게시판", BoardKind.VIDEO, null, true);
            Board board4 = Board.createBoard("장터 게시판", BoardKind.MARKET, null, true);
            Board board5 = Board.createBoard("맛집 게시판", BoardKind.FOOD, null, true);

            em.persist(board2);
            em.persist(board3);
            em.persist(board4);
            em.persist(board5);

        }
    }
}
