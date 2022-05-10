package FIS.iLUVit.init;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
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

            Center center1 = Center.createCenter("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                    3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                    false, 0, "gkgkgkgk", 0, 0, "얼쥡", null, null, null, null, null, null);
            Center center2 = Center.createCenter("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                    3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                    false, 0, "gkgkgkgk", 0, 0, "얼쥡", null, null, null, null, null, null);
            Center center3 = Center.createCenter("떡잎유치원", "민병관", "민병관", "민간", "ㅁㄴㅇ", "2022-02-20", "02-123-1234", "www.www.www", "09:00", "19:00",
                    3, 90, "서울시 금천구 뉴티캐슬", "152-052", new Area("서울시", "금천구"), 123.123, 123.123, "흙찡구놀이, 비둘기잡기", 99999, 88888, LocalDate.now(), false,
                    false, 0, "gkgkgkgk", 0, 0, "얼쥡", null, null, null, null, null, null);
            em.persist(center1);
            em.persist(center2);
            em.persist(center3);

            Teacher teacher1 = Teacher.createTeacher("asd", "asd", encoder.encode("asd"), "asd", false, "asd@asd.com", "asd", Auth.TEACHER, Approval.WAITING);
            Teacher teacher2 = Teacher.createTeacher("sad", "sad", encoder.encode("asd"), "sad", false, "sad@sad.com", "sad", Auth.TEACHER, Approval.WAITING);
            Teacher teacher3 = Teacher.createTeacher("dsa", "dsa", encoder.encode("asd"), "dsa", false, "dsa@dsa.com", "dsa", Auth.TEACHER, Approval.WAITING);
            Teacher teacher4 = Teacher.createTeacher("ddd", "ddd", encoder.encode("asd"), "ddd", false, "ddd@ddd.com", "ddd", Auth.DIRECTOR, Approval.ACCEPT);
            Teacher teacher5 = Teacher.createTeacher("sss", "sss", encoder.encode("asd"), "sss", false, "sss@sss.com", "sss", Auth.DIRECTOR, Approval.WAITING);
            em.persist(teacher1);
            em.persist(teacher2);
            em.persist(teacher3);
            em.persist(teacher4);
            em.persist(teacher5);

            Parent parent1 = Parent.createParent("qwe", "qwe", encoder.encode("asd"), "qwe", false, "qwe@qwe.com", "qwe", new Theme(), 5, Auth.PARENT);
            Parent parent2 = Parent.createParent("ewq", "ewq", encoder.encode("asd"), "ewq", false, "ewq@ewq.com", "ewq", new Theme(), 5, Auth.PARENT);
            Parent parent3 = Parent.createParent("weq", "weq", encoder.encode("asd"), "weq", false, "weq@weq.com", "weq", new Theme(), 5, Auth.PARENT);
            em.persist(parent1);
            em.persist(parent2);
            em.persist(parent3);

            Child child1 = Child.createChild("zxc", "zxc", Approval.WAITING);
            Child child2 = Child.createChild("zxc", "zxc", Approval.ACCEPT);
            Child child3 = Child.createChild("zxc", "zxc", Approval.ACCEPT);
            em.persist(child1);
            em.persist(child2);
            em.persist(child3);
        }
    }
}
