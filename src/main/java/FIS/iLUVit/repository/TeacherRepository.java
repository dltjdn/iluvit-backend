package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByNickName(String nickname);

    @Query("select distinct t " +
            "from Teacher t " +
            "join fetch t.center c " +
            "left join fetch c.teachers " +
            "where t.id =:userId " +
            "and t.auth = 'DIRECTOR'")
    Optional<Teacher> findDirectorByIdWithCenterWithTeacher(@Param("userId") Long userId);


    @Query("select t " +
            "from Teacher t " +
            "join fetch t.center c " +
            "where c.id =:centerId " +
            "and t.id <>:userId")
    List<Teacher> findTeacherListByCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    @Modifying
    @Query("update Teacher t " +
            "set t.approval = 'ACCEPT' " +
            "where t.id =:teacherId " +
            "and t.center.id =:centerId")
    void acceptTeacher(@Param("teacherId") Long teacherId, @Param("centerId") Long centerId);

    @Modifying
    @Query("update Teacher t " +
            "set t.center.id = null, t.auth = 'TEACHER' " +
            "where t.id =:teacherId")
    void exitCenter(@Param("teacherId") Long teacherId);

    @Query("select distinct t " +
            "from Teacher t " +
            "join fetch t.center c " +
            "left join fetch c.children cc " +
            "join fetch cc.parent " +
            "where t.id =:userId " +
            "and t.approval = 'ACCEPT'")
    Optional<Teacher> findByIdWithCenterWithChildWithParent(@Param("userId") Long userId);

    @Query("select distinct t " +
            "from Teacher t " +
            "join fetch t.center c " +
            "left join fetch c.boards " +
            "where t.id =:userId ")
    Optional<Teacher> findByIdAndAssign(@Param("userId") Long userId);

    @Query("select t " +
            "from Teacher t " +
            "where t.id =:userId " +
            "and t.center is null")
    Optional<Teacher> findByIdAndNotAssign(@Param("userId") Long userId);

    @Modifying
    @Query("update Teacher t " +
            "set t.center.id =:centerId, t.approval = 'WAITING' " +
            "where t.id =:userId ")
    void assignCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    @Query("select t from Teacher t where t.center.id =:centerId")
    List<Center> findByCenter(@Param("centerId") Long centerId);

    @Query("select distinct t " +
            "from Teacher t " +
            "join fetch t.center c " +
            "join fetch c.teachers " +
            "where t.id =:userId")
    Optional<Teacher> findByIdWithCenterWithTeacher(@Param("userId") Long userId);

    @Query("select t " +
            "from Teacher t " +
            "where t.center.id =:centerId " +
            "and t.auth = 'DIRECTOR'")
    List<Teacher> findDirectorByCenter(@Param("centerId") Long centerId);
}
