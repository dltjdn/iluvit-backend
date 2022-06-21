package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.websocket.server.PathParam;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    @Query("select teacher from Teacher teacher " +
            "join fetch teacher.center as center " +
            "join fetch center.presentations as presentation " +
            "where teacher.id = :userId " +
            "and presentation.endDate <= :date")
    Optional<Teacher> findTeacherAndJoinPresentationById(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("select teacher from Teacher teacher " +
            "join fetch teacher.center " +
            "where teacher.id = :userId")
    Optional<Teacher> findTeacherById(@Param("userId") Long userId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("select c from Child c join c.parent p" +
            " where p.id = :parentId")
    List<Child> findChildren(@Param("parentId") Long parentId);

    Optional<User> findByLoginIdAndPhoneNumber(String loginId, String phoneNumber);

    @Query("select user " +
            "from User user ")
    List<User> findByIdTest();
}
