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

            Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);


            // 시설 추가z
            Kindergarten center1 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                    3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                    false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, theme, null);
            Kindergarten center2 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                    3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                    false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, theme, null);
            Kindergarten center3 = Kindergarten.createKindergarten("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                    3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                    false, 0, "gkgkgkgk", 3, 0, "얼쥡", null, null, null, null, theme, null);
            em.persist(center1);
            em.persist(center2);
            em.persist(center3);

            // 선생 추가
            Teacher teacher1 = Teacher.createTeacher("asd", "asd", encoder.encode("asd"), "asd", false, "asd@asd.com", "asd", Auth.TEACHER, Approval.WAITING, center1, "서울특별시", "구로구 벚꽃로 68길 10");
            Teacher teacher2 = Teacher.createTeacher("sad", "sad", encoder.encode("asd"), "sad2", false, "sad@sad.com", "sad", Auth.TEACHER, Approval.WAITING, center1, "서울특별시", "구로구 벚꽃로 68길 10");
            Teacher teacher3 = Teacher.createTeacher("dsa", "dsa", encoder.encode("asd"), "dsa3", false, "dsa@dsa.com", "dsa", Auth.TEACHER, Approval.WAITING, center1, "서울특별시", "구로구 벚꽃로 68길 10");
            Teacher teacher4 = Teacher.createTeacher("ddd", "ddd", encoder.encode("asd"), "ddd4", false, "ddd@ddd.com", "ddd", Auth.DIRECTOR, Approval.ACCEPT, center2, "서울특별시", "구로구 벚꽃로 68길 10");
            Teacher teacher5 = Teacher.createTeacher("sss", "sss", encoder.encode("asd"), "sss5", false, "sss@sss.com", "sss", Auth.DIRECTOR, Approval.WAITING, center2, "서울특별시", "구로구 벚꽃로 68길 10");
            em.persist(teacher1);
            em.persist(teacher2);
            em.persist(teacher3);
            em.persist(teacher4);
            em.persist(teacher5);

            // 학부모 추가
            Parent parent1 = Parent.createParent("qwe", "qwe", encoder.encode("asd"), "asd6", false, "qwe@qwe.com", "qwe", theme, 5, Auth.PARENT, "서울특별시", "구로구 벚꽃로 68길 10");
            Parent parent2 = Parent.createParent("ewq", "ewq", encoder.encode("asd"), "ewq7", false, "ewq@ewq.com", "ewq", theme, 5, Auth.PARENT, "서울특별시", "구로구 벚꽃로 68길 10");
            Parent parent3 = Parent.createParent("weq", "weq", encoder.encode("asd"), "weq8", false, "weq@weq.com", "weq", theme, 5, Auth.PARENT, "서울특별시", "구로구 벚꽃로 68길 10");

            em.persist(parent1);
            em.persist(parent2);
            em.persist(parent3);

            // 아이 추가
            Child child1 = Child.createChild("zxc", "zxc", Approval.WAITING, parent1);
            Child child2 = Child.createChild("zxc", "zxc", Approval.ACCEPT, parent1);
            Child child3 = Child.createChild("zxc", "zxc", Approval.ACCEPT, parent1);
            child1.mappingCenter(center1);
            child2.mappingCenter(center2);
            em.persist(child1);
            em.persist(child2);
            em.persist(child3);

            Review review1 = Review.createReview("친절해요", 5, false, parent1, center1);
            Review review2 = Review.createReview("좋아요", 5, false, parent2, center1);
            Review review3 = Review.createReview("거리가 가까워요", 4, false, parent3, center1);
            Review review4 = Review.createReview("가격이 싸요", 5, false, parent1, center2);
            Review review5 = Review.createReview("좋아요", 4, true, parent2, center2);
            Review review6 = Review.createReview("놀이터가 좋아요", 5, false, parent3, center3);
            em.persist(review1);
            em.persist(review2);
            em.persist(review3);
            em.persist(review4);
            em.persist(review5);
            em.persist(review6);

            ReviewHeart reviewHeart1 = new ReviewHeart(review1, parent1);
            ReviewHeart reviewHeart2 = new ReviewHeart(review1, parent2);
            ReviewHeart reviewHeart3 = new ReviewHeart(review1, parent3);
            em.persist(reviewHeart1);
            em.persist(reviewHeart2);
            em.persist(reviewHeart3);
            // 설명회 추가
            Presentation presentation1 = Presentation.createPresentation(LocalDate.now(), LocalDate.now(), "이승범네 집", "설명회 입니다.", 5, 3, center1);
            Presentation presentation2 = Presentation.createPresentation(LocalDate.now(), LocalDate.now(), "이승범네 집", "설명회 입니다.", 5, 3, center1);
            Presentation presentation3 = Presentation.createPresentation(LocalDate.now(), LocalDate.now(), "이승범네 집", "설명회 입니다.", 5, 3, center2);
            em.persist(presentation1);
            em.persist(presentation2);
            em.persist(presentation3);

            // 설명회의 회차 추가
            PtDate ptDate1 = PtDate.createPtDate(LocalDate.now(), "오후 1시", 1, 0, presentation1);
            PtDate ptDate2 = PtDate.createPtDate(LocalDate.now(), "오후 2시", 3, 0, presentation2);
            PtDate ptDate3 = PtDate.createPtDate(LocalDate.now(), "오후 3시", 2, 0, presentation3);
            em.persist(ptDate1);
            em.persist(ptDate2);
            em.persist(ptDate3);

//            Board board1 = Board.createBoard("HOT 게시물", BoardKind.NORMAL, null, false);
            Board board2 = Board.createBoard("자유 게시판", BoardKind.NORMAL, null, true);
            Board board3 = Board.createBoard("영상 게시판", BoardKind.VIDEO, null, true);
            Board board4 = Board.createBoard("장터 게시판", BoardKind.MARKET, null, true);
            Board board5 = Board.createBoard("맛집 게시판", BoardKind.FOOD, null, true);
            Board board6 = Board.createBoard("공지 게시판", BoardKind.NORMAL, center1, true);
            Board board7 = Board.createBoard("놀이 게시판", BoardKind.NORMAL, center2, false);
//            em.persist(board1);
            em.persist(board2);
            em.persist(board3);
            em.persist(board4);
            em.persist(board5);
            em.persist(board6);
            em.persist(board7);

            Bookmark bookmark1 = new Bookmark(0, board2, parent1);
            Bookmark bookmark2 = new Bookmark(1, board3, parent1);
            Bookmark bookmark3 = new Bookmark(2, board4, parent1);
            Bookmark bookmark4 = new Bookmark(3, board5, parent1);
            Bookmark bookmark5 = new Bookmark(4, board6, parent1);
            Bookmark bookmark6 = new Bookmark(5, board7, parent1);
            em.persist(bookmark1);
            em.persist(bookmark2);
            em.persist(bookmark3);
            em.persist(bookmark4);
            em.persist(bookmark5);
            em.persist(bookmark6);


            Post post1 = new Post("제목이다", "내용이다", false, 0, 0, 0, 0, board2, teacher1);
            Post post2 = new Post("안녕", "먹칠하잖아", false, 0, 0, 0, 0, board2, parent1);
            Post post3 = new Post("게시글제목", "계속먹칠하잖아", false, 0, 0, 0, 0, board2, teacher2);
            Post post4 = new Post("타이틀", "abcdefg", false, 0, 0, 0, 0, board2, parent2);
            Post post5 = new Post("다와가", "때려밟았지마티즈엑셀", false, 0, 0, 0, 0, board2, teacher3);
            Post post6 = new Post("집에가고 싶다", "집에가자 좀", false, 0, 0, 0, 0, board6, parent1);
            Post post7 = new Post("a sdffg", "ff 좀", false, 0, 0, 0, 0, board2, parent1);
            Post post8 = new Post("ba sdffg", "a", false, 0, 0, 0, 0, board2, parent1);
            Post post9 = new Post("b sdffg", "b", false, 0, 0, 0, 0, board7, parent1);
            Post post10 = new Post("c sdffg", "c", false, 0, 0, 0, 0, board3, parent1);
            Post post11 = new Post("d sdffg", "d 좀", false, 0, 0, 0, 0, board3, parent1);
            Post post12 = new Post("e sdffg", "e 좀", false, 0, 0, 0, 0, board4, parent1);
            Post post13 = new Post("f sdffg", "f 좀", false, 0, 0, 0, 0, board4, parent1);
            Post post14 = new Post("g sdffg", "g 좀", false, 0, 0, 0, 0, board4, parent1);
            Post post15 = new Post("h sdffg", "h 좀", false, 0, 0, 0, 0, board5, parent1);
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
            em.persist(post14);
            em.persist(post15);

            PostHeart postHeart1 = new PostHeart(teacher4, post1);
            PostHeart postHeart2 = new PostHeart(parent3, post1);
            PostHeart postHeart3 = new PostHeart(teacher1, post2);
            PostHeart postHeart4 = new PostHeart(parent2, post2);
            em.persist(postHeart1);
            em.persist(postHeart2);
            em.persist(postHeart3);
            em.persist(postHeart4);

            Comment comment1 = new Comment(false, "댓글이다", post1, parent1);
            Comment comment2 = new Comment(false, "댓글이다2", post1, parent2);
            Comment comment3 = new Comment(false, "댓글이다3", post1, parent3);
            comment2.updateParentComment(comment1);
            comment3.updateParentComment(comment1);
            Comment comment4 = new Comment(false, "안녕하세요", post2, teacher1);
            Comment comment5 = new Comment(false, "댓글내용", post3, teacher2);
            Comment comment6 = new Comment(false, "adsasdfasfd", post4, teacher4);
            em.persist(comment1);
            em.persist(comment2);
            em.persist(comment3);
            em.persist(comment4);
            em.persist(comment5);
            em.persist(comment6);

            CommentHeart commentHeart1 = new CommentHeart(teacher1, comment1);
            CommentHeart commentHeart2 = new CommentHeart(teacher2, comment1);
            CommentHeart commentHeart3 = new CommentHeart(teacher3, comment1);
            CommentHeart commentHeart4 = new CommentHeart(teacher4, comment2);
            CommentHeart commentHeart5 = new CommentHeart(parent1, comment2);
            em.persist(commentHeart1);
            em.persist(commentHeart2);
            em.persist(commentHeart3);
            em.persist(commentHeart4);
            em.persist(commentHeart5);
        }
    }
}
