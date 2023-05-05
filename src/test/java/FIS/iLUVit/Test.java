package FIS.iLUVit;

import FIS.iLUVit.domain.iluvit.Teacher;
import FIS.iLUVit.domain.iluvit.User;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
class UserTest {

    @Autowired
    EntityManager em;

    @Test
    void asd(){
        List<User> users = em.createQuery("select t from Teacher t", User.class).getResultList();
        for (User user : users) {
            System.out.println("user = " + user.getDtype());
        }
        Teacher teacher = Teacher.createTeacher("qaz", "qaz", "qaz", "01012341234", "qaz@qaz.com", "qaz", Auth.TEACHER, Approval.WAITING, null, "서울특별시", "구로구 벚꽃로 68길 10");
        em.persist(teacher);
        em.flush();
        em.clear();
        User user = em.createQuery("select u from User u where u.id =: id", User.class)
                .setParameter("id", teacher.getId())
                .getSingleResult();
        System.out.println("teacher = " + user.getDtype());
    }

    @Test
    void test() {
        LocalDateTime a = LocalDateTime.now();
        LocalDateTime b = LocalDateTime.now();
        if (a == b) {
            System.out.println("a==b is true");
        } else {
            System.out.println("a==b is false");
        }
    }
}
