package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.TeacherApprovalListResponse;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Auth;
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
    Optional<Teacher> findDirectorByIdWithCenter(@Param("userId") Long userId);


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
    void approveTeacher(@Param("teacherId") Long teacherId, @Param("centerId") Long centerId);

    @Modifying
    @Query("update Teacher t " +
            "set t.center.id = null " +
            "where t.id =:teacherId")
    void fireTeacher(@Param("teacherId") Long teacherId);
}
