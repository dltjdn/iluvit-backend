package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /*
        닉네임으로 선생님을 조회합니다.
     */
    Optional<Teacher> findByNickName(String nickname);

    /*
        사용자 id를 파라미터로 받아서 부모가 있는 자식이 있는 센터가 있는 ID로 선생님을 조회합니다.
     */
    @Query("select distinct t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.approval = 'ACCEPT'")
    Optional<Teacher> findByIdWithCenterWithChildWithParent(@Param("userId") Long userId);

    /*
        사용자 id를 파라미터로 받아서 부모가 있는 자식이 있는 센터가 있는 ID로 선생님을 조회합니다.
     */
    @Query("select t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.center is null")
    Optional<Teacher> findByIdAndNotAssign(@Param("userId") Long userId);

    /*
        시설 id를 파라미터로 받아서 시설 id로 조회합니다.
     */
    @Query("select t from Teacher t where t.center.id =:centerId")
    List<Teacher> findByCenterId(@Param("centerId") Long centerId);

    /*
        id로 조회합니다.
     */
    Optional<Teacher> findById(Long userId);

    /*
        시설 id를 파라미터로 받아서 시설로 감독을 조회합니다.
     */
    @Query("select t " +
            "from Teacher t " +
            "where t.center.id =:centerId " +
            "and t.auth = 'DIRECTOR'")
    List<Teacher> findDirectorByCenter(@Param("centerId") Long centerId);

    /*
        사용자 id를 파라미터로 받아서 감독을 조회합니다.
     */
    @Query("select t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.auth = 'DIRECTOR'")
    Optional<Teacher> findDirectorById(@Param("userId") Long userId);
}
