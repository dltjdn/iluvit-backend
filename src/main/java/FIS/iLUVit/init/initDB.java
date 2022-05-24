package FIS.iLUVit.init;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
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

    @PostConstruct
    public void init() {
        initService.dbInit();
    }


    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        private final BCryptPasswordEncoder encoder;

        public void dbInit() {

            Theme theme = new Theme(true, false, true, false, true, true, false, false, false, false, true, false, false, false, false, true, false);


            // 시설 추가
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
            Teacher teacher1 = Teacher.createTeacher("asd", "asd", encoder.encode("asd"), "asd", false, "asd@asd.com", "asd", Auth.TEACHER, Approval.WAITING, center1);
            Teacher teacher2 = Teacher.createTeacher("sad", "sad", encoder.encode("asd"), "sad", false, "sad@sad.com", "sad", Auth.TEACHER, Approval.WAITING, center1);
            Teacher teacher3 = Teacher.createTeacher("dsa", "dsa", encoder.encode("asd"), "dsa", false, "dsa@dsa.com", "dsa", Auth.TEACHER, Approval.WAITING, center1);
            Teacher teacher4 = Teacher.createTeacher("ddd", "ddd", encoder.encode("asd"), "ddd", false, "ddd@ddd.com", "ddd", Auth.DIRECTOR, Approval.ACCEPT, center2);
            Teacher teacher5 = Teacher.createTeacher("sss", "sss", encoder.encode("asd"), "sss", false, "sss@sss.com", "sss", Auth.DIRECTOR, Approval.WAITING, center2);
            em.persist(teacher1);
            em.persist(teacher2);
            em.persist(teacher3);
            em.persist(teacher4);
            em.persist(teacher5);

            // 학부모 추가
            Parent parent1 = Parent.createParent("qwe", "qwe", encoder.encode("asd"), "qwe", false, "qwe@qwe.com", "qwe", theme, 5, Auth.PARENT);
            Parent parent2 = Parent.createParent("ewq", "ewq", encoder.encode("asd"), "ewq", false, "ewq@ewq.com", "ewq", theme, 5, Auth.PARENT);
            Parent parent3 = Parent.createParent("weq", "weq", encoder.encode("asd"), "weq", false, "weq@weq.com", "weq", theme, 5, Auth.PARENT);
            em.persist(parent1);
            em.persist(parent2);
            em.persist(parent3);

            // 아이 추가
            Child child1 = Child.createChild("zxc", "zxc", Approval.WAITING, parent1);
            Child child2 = Child.createChild("zxc", "zxc", Approval.ACCEPT, parent1);
            Child child3 = Child.createChild("zxc", "zxc", Approval.ACCEPT, parent1);
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
            PtDate ptDate2 = PtDate.createPtDate(LocalDate.now(), "오후 2시", 3, 0, presentation1);
            PtDate ptDate3 = PtDate.createPtDate(LocalDate.now(), "오후 3시", 2, 0, presentation2);
            em.persist(ptDate1);
            em.persist(ptDate2);
            em.persist(ptDate3);
        }
    }
}
