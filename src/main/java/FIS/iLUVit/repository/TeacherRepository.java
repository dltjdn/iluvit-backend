package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /*
        닉네임으로 교사를 조회합니다.
     */
    Optional<Teacher> findByNickName(String nickname);

    /*
        선생님 id가 userId와 같고 auth가 DIRECTOR와 같은 교사를 조회합니다.
     */
    @Query("select distinct t " +
            "from Teacher t " +
            "join fetch t.center c " +
            "left join fetch c.teachers " +
            "where t.id =:userId " +
            "and t.auth = 'DIRECTOR'")
    Optional<Teacher> findDirectorByIdWithCenterWithTeacher(@Param("userId") Long userId);

    /*
        선생님 id가 userId와 같고 approval가 ACCEPT와 같은 교사를 조회합니다.
     */
    @Query("select distinct t " +
            "from Teacher t " +
            "join fetch t.center c " +
            "left join fetch c.children cc " +
            "left join fetch cc.parent " +
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
        교수 시설 id가 시설 id와 같은 시설 리스트를 조회합니다.
     */
    @Query("select t from Teacher t where t.center.id =:centerId")
    List<Center> findByCenter(@Param("centerId") Long centerId);

    /*
        userId와 일치하는 교사를 조회하고, 해당 교사의 센터와 센터의 교사들을 함께 조회하여 교수를 불러옵니다.
     */
    @Query("select distinct t " +
            "from Teacher t " +
            "join fetch t.center c " +
            "left join fetch c.teachers " +
            "where t.id =:userId")
    Optional<Teacher> findByIdWithCenterWithTeacher(@Param("userId") Long userId);

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
}
