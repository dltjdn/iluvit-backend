package FIS.iLUVit;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.repository.CenterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public class PersistenceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private CenterRepository centerRepository;

    @Test
    void testContext(){
        Center center = em.createQuery(
                "select distinct c " +
                        "from Center c " +
                        "join fetch c.teachers " +
                        "where c.id = 1", Center.class)
                .getSingleResult();
        for (Teacher teacher : center.getTeachers()) {
            System.out.println("teacher.getName() = " + teacher.getName());
        }
        center.getTeachers().remove(0);
        center = centerRepository.findById(1L).orElse(null);
        for (Teacher teacher : center.getTeachers()) {
            System.out.println("teacher.getName() = " + teacher.getName());
        }

    }

    @Test
    public void dd() throws Exception {
        //given
        Center center = new Center();
        centerRepository.save(center);
        System.out.println("-=========== 나가나? ============= ");
        em.flush();
        System.out.println("-=========== 나가나? ============= ");
        //when
        centerRepository.findById(center.getId());
        //then
    }
}
