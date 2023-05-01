package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByNickName(String nickname);

    @Query("select distinct t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.approval = 'ACCEPT'")
    Optional<Teacher> findByIdWithCenterWithChildWithParent(@Param("userId") Long userId);

    @Query("select t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.center is null")
    Optional<Teacher> findByIdAndNotAssign(@Param("userId") Long userId);

    @Query("select t from Teacher t where t.center.id =:centerId")
    List<Center> findByCenter(@Param("centerId") Long centerId);

    Optional<Teacher> findById(Long userId);

    @Query("select t " +
            "from Teacher t " +
            "where t.center.id =:centerId " +
            "and t.auth = 'DIRECTOR'")
    List<Teacher> findDirectorByCenter(@Param("centerId") Long centerId);

    @Query("select t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.auth = 'DIRECTOR'")
    Optional<Teacher> findDirectorById(@Param("userId") Long userId);
}
