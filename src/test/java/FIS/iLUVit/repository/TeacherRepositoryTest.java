package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
public class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private EntityManager em;

    private Center center1;
    private Center center2;
    private Center center3;
    private Teacher teacher1;
    private Teacher teacher2;
    private Teacher teacher3;
    private Teacher teacher4;
    private Teacher teacher5;
    private Teacher teacher6;

    @BeforeEach
    public void init() {
        center1 = Creator.createCenter("center1", true, false, Creator.createTheme());
        center2 = Creator.createCenter("center2", true, false, Creator.createTheme());
        center3 = Creator.createCenter("center3", true, false, Creator.createTheme());
        teacher1 = Creator.createTeacher(null, "teacher1", center1, Approval.ACCEPT, Auth.DIRECTOR);
        teacher2 = Creator.createTeacher(null, "teacher2", center1, Approval.ACCEPT, Auth.TEACHER);
        teacher3 = Creator.createTeacher(null, "teacher3", center1, Approval.WAITING, Auth.TEACHER);
        teacher4 = Creator.createTeacher(null, "teacher4", center2, Approval.ACCEPT, Auth.DIRECTOR);
        teacher5 = Creator.createTeacher(null, "teacher5", null, null, Auth.TEACHER);
        teacher6 = Creator.createTeacher(null, "teacher6", center2, Approval.ACCEPT, Auth.DIRECTOR);
        em.persist(center1);
        em.persist(center2);
        em.persist(center3);
        em.persist(teacher1);
        em.persist(teacher2);
        em.persist(teacher3);
        em.persist(teacher4);
        em.persist(teacher5);
        em.persist(teacher6);
    }

    @Nested
    @DisplayName("findByIdAndNotAssign")
    class findByIdAndNotAssign{

        @Test
        public void 해당교사가속해있는시설이있는경우() {
            // given
            em.flush();
            em.clear();
            // when
            Teacher result = teacherRepository.findByIdAndNotAssign(teacher1.getId()).orElse(null);
            // then
            assertThat(result).isNull();
        }

        @Test
        public void 해당교사가속해있는시설이없는경우() {
            // given
            em.flush();
            em.clear();
            // when
            Teacher result = teacherRepository.findByIdAndNotAssign(teacher5.getId()).orElse(null);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(teacher5.getId());
        }
    }

    @Test
    public void findDirectorByCenter() {
        // given
        em.flush();
        em.clear();
        // when
        List<Teacher> result = teacherRepository.findDirectorByCenter(center2.getId());
        // then
        assertThat(result.size()).isEqualTo(2);
        for (Teacher teacher : result) {
            assertThat(teacher.getCenter().getId()).isEqualTo(center2.getId());
            assertThat(teacher.getAuth()).isEqualTo(Auth.DIRECTOR);
        }
    }

    @Test
    public void findDirectorById() {
        // given
        em.flush();
        em.clear();
        // when
        Teacher result = teacherRepository.findDirectorById(teacher1.getId()).orElse(null);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(teacher1.getId());
        assertThat(result.getCenter().getId()).isEqualTo(teacher1.getCenter().getId());
    }

    @Test
    public void findByIdWithCenterWithTeacher() {
        // given
        em.flush();
        em.clear();
        // when
        Teacher result = teacherRepository.findByIdWithCenterWithTeacher(teacher1.getId()).orElse(null);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(teacher1.getId());
        assertThat(result.getCenter().getId()).isEqualTo(center1.getId());
        assertThat(result.getCenter().getTeachers().size()).isEqualTo(3);
    }

    @Nested
    @DisplayName("findDirectorByIdWithCenterWithTeacher")
    class findDirectorByIdWithCenterWithTeacher{
        @Test
        public void 정상적인요청() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            em.flush();
            em.clear();
            // when
            Teacher result = teacherRepository.findDirectorByIdWithCenterWithTeacher(teacher1.getId()).orElse(null);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(teacher1.getId());
            assertThat(result.getCenter().getTeachers().size()).isEqualTo(3);
        }

        @Test
        public void 원장이아닌사용자의요청() {
            // given
            center1.getTeachers().add(teacher1);
            center1.getTeachers().add(teacher2);
            center1.getTeachers().add(teacher3);
            em.flush();
            em.clear();
            // when
            Teacher result = teacherRepository.findDirectorByIdWithCenterWithTeacher(teacher2.getId()).orElse(null);
            // then
            assertThat(result).isNull();
        }
    }


}
