package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.Child;
import FIS.iLUVit.domain.iluvit.Prefer;
import FIS.iLUVit.domain.iluvit.Teacher;
import FIS.iLUVit.domain.iluvit.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByNickName(String nickname);

    Optional<User> findByLoginIdOrNickName(String loginId, String nickName);

    @Query("select teacher from Teacher teacher " +
            "join fetch teacher.center " +
            "where teacher.id = :userId")
    Optional<Teacher> findTeacherById(@Param("userId") Long userId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("select c from Child c join c.parent p" +
            " where p.id = :parentId")
    List<Child> findChildren(@Param("parentId") Long parentId);

    @Query("select c from Child c " +
            "join c.parent p " +
            "join fetch c.center ct " +
            " where p.id = :parentId")
    List<Child> findChildrenWithCenter(@Param("parentId") Long parentId);

    Optional<User> findByLoginIdAndPhoneNumber(String loginId, String phoneNumber);

    @Query("select user " +
            "from User user ")
    List<User> findByIdTest();

    @Query("select teacher from Teacher teacher " +
            "where teacher.center =:center")
    List<User> findTeacherByCenter(@Param("center") Center center);

    @Query("select prefer from Prefer prefer " +
            "join fetch prefer.center " +
            "where prefer.center = :center")
    List<Prefer> getUserPreferByCenterId(@Param("center") Center center);

    Optional<User> findByIdAndPhoneNumber(Long id, String phoneNumber);
}
