package FIS.iLUVit.repository;

import FIS.iLUVit.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.websocket.server.PathParam;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /*
        로그인 id로 사용자를 조회합니다.
     */
    Optional<User> findByLoginId(String loginId);

    /*
        닉네임으로 사용자를 조회합니다.
     */
    Optional<User> findByNickName(String nickname);

    /*
        로그인 id 또는 닉네임으로 사용자를 조회합니다.
     */
    Optional<User> findByLoginIdOrNickName(String loginId, String nickName);


    /*
        사용자 id를 파라미터로 받아서 id로 선생님을 조회합니다.
     */
    @Query("select distinct teacher from Teacher teacher " +
            "join fetch teacher.center as center " +
            "join fetch center.presentations as presentation " +
            "where teacher.id = :userId " +
            "and presentation.endDate <= :date")
    Optional<Teacher> findTeacherAndJoinPresentationById(@Param("userId") Long userId, @Param("date") LocalDate date);


    @Query("select teacher from Teacher teacher " +
            "join fetch teacher.center " +
            "where teacher.id = :userId")
    Optional<Teacher> findTeacherById(@Param("userId") Long userId);

    /*
        전화번호로 사용자를 조회합니다.
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /*
        부모 id를 파라미터로 받아서 아이를 조회합니다.
     */
    @Query("select c from Child c join c.parent p" +
            " where p.id = :parentId")
    List<Child> findChildren(@Param("parentId") Long parentId);

    /*
        부모 id를 파라미터로 받아서 시설에 있는 아이를 조회합니다.
     */
    @Query("select c from Child c " +
            "join c.parent p " +
            "join fetch c.center ct " +
            " where p.id = :parentId")
    List<Child> findChildrenWithCenter(@Param("parentId") Long parentId);

    /*
        로그인 id 및 전화번호로 조회합니다.
     */
    Optional<User> findByLoginIdAndPhoneNumber(String loginId, String phoneNumber);

    /*
        부모 id를 파라미터로 받아서 아이를 조회합니다.
     */
    @Query("select user " +
            "from User user ")
    List<User> findByIdTest();

    /*
        시설을 파라미터로 받아서 시설로 선생님을 조회합니다.
     */
    @Query("select teacher from Teacher teacher " +
            "where teacher.center =:center")
    List<User> findTeacherByCenter(@Param("center") Center center);

    /*
        시설을 파라미터로 받아서 시설 id로 사용자 선호를 받아옵니다.
     */
    @Query("select prefer from Prefer prefer " +
            "join fetch prefer.center " +
            "where prefer.center = :center")
    List<Prefer> getUserPreferByCenterId(@Param("center") Center center);

    /*
        id 및 전화번호로 조회합니다.
     */
    Optional<User> findByIdAndPhoneNumber(Long id, String phoneNumber);
}
