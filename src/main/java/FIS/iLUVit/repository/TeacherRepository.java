package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * 해당 닉네임으로 교사를 조회합니다
     */
    Optional<Teacher> findByNickName(String nickname);

    /**
     * 해당 시설로 교사를 조회합니다
     */
    List<Teacher> findByCenter(Center center);
    /*
        선생님 id가 userId와 같고 approval가 ACCEPT와 같은 교사를 조회합니다.
     */
    @Query("select distinct t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.approval = 'ACCEPT'")
    Optional<Teacher> findByIdWithCenterWithChildWithParent(@Param("userId") Long userId);

    /*
        교수 id가 userId와 같고 교수가 속해있는 시설이 null인 교수를 조회합니다.
     */
    @Query("select t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.center is null")
    Optional<Teacher> findByIdAndNotAssign(@Param("userId") Long userId);

    /*
        센터별 교사 리스트를 조회합니다
     */
    @Query("select t from Teacher t where t.center.id =:centerId")
    List<Center> findByCenter(@Param("centerId") Long centerId);

    /*
        교수가 속한 시설 id가 시설 id와 같고 교수 auth가 DIRECTOR인 교수 리스트를 조회합니다.
     */
    @Query("select t " +
            "from Teacher t " +
            "where t.center.id =:centerId " +
            "and t.auth = 'DIRECTOR'")
    List<Teacher> findDirectorByCenter(@Param("centerId") Long centerId);

    /*
        교수가 id가 사용자 id와 같고 교수 auth가 DIRECTOR인 교수를 조회합니다.
     */
    @Query("select t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.auth = 'DIRECTOR'")
    Optional<Teacher> findDirectorById(@Param("userId") Long userId);

    /*
        센터별 승인된 교사를 조회합니다
     */
    @Query("select t " +
            "from Teacher t " +
            "where t.center.id =:centerId " +
            "and t.approval = 'ACCEPT' ")
    List<Teacher> findByCenterWithApproval(@Param("centerId") Long centerId);

}
