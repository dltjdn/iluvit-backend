package FIS.iLUVit;

import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
        Teacher teacher = Teacher.createTeacher("qaz", "qaz", "qaz", "01012341234",
                false, "qaz@qaz.com", "qaz", Auth.TEACHER, Approval.WAITING);
        em.persist(teacher);
        em.flush();
        em.clear();
        User user = em.createQuery("select u from User u where u.id =: id", User.class)
                .setParameter("id", teacher.getId())
                .getSingleResult();
        System.out.println("teacher = " + user.getDtype());
    }

}
