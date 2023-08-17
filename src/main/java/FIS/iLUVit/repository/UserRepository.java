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
        로그인 id로 User를 불러옵니다.
     */
    Optional<User> findByLoginId(String loginId);

    /*
        닉네임으로 User를 불러옵니다.
     */
    Optional<User> findByNickName(String nickname);

    /*
        로그인 id 또는 닉네임으로 User를 불러옵니다.
     */
    Optional<User> findByLoginIdOrNickName(String loginId, String nickName);


    /*
        교수 id가 사용자 id와 같은 Teacher를 조회합니다.
     */
    @Query("select teacher from Teacher teacher " +
            "join fetch teacher.center " +
            "where teacher.id = :userId")
    Optional<Teacher> findTeacherById(@Param("userId") Long userId);

    /*
        전화번호로 User를 조회합니다.
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /*
        시설에 속해있는 부모 id가 부모 id와 같은 Child 리스트를 조회합니다.
     */
    @Query("select c from Child c join c.parent p" +
            " where p.id = :parentId")
    List<Child> findChildren(@Param("parentId") Long parentId);

    /*
        시설에 속해있는 아이의 부모 id가 부모 id와 같은 Child 리스트를 조회합니다.
     */
    @Query("select c from Child c " +
            "join c.parent p " +
            "join fetch c.center ct " +
            " where p.id = :parentId")
    List<Child> findChildrenWithCenter(@Param("parentId") Long parentId);

    /*
        전화번호와 로그인 id로 User를 조회합니다.
     */
    Optional<User> findByLoginIdAndPhoneNumber(String loginId, String phoneNumber);

    /*
        모든 사용자를 조회하여 User 리스트로 불러옵니다.
     */
    @Query("select user " +
            "from User user ")
    List<User> findByIdTest();

    /*
        특정 센터에 속한 교수를 조회하여 User 리스트로 불러옵니다.
     */
    @Query("select teacher from Teacher teacher " +
            "where teacher.center = :center")
    List<User> findTeacherByCenter(@Param("center") Center center);

    /*
        즐겨찾기한 시설과 시설을 비교하여 같은 것을 즐겨찾기 리스트로 불러옵니다.
     */
    @Query("select prefer from Prefer prefer " +
            "join fetch prefer.center " +
            "where prefer.center = :center")
    List<Prefer> getUserPreferByCenterId(@Param("center") Center center);

    /*
        전화번호와 사용자 id로 User를 받아옵니다.
     */
    Optional<User> findByIdAndPhoneNumber(Long id, String phoneNumber);
}
