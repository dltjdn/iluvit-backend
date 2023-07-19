package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

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

    /**
     * 해당 user id를 가지고 center 필드가 null인 교사를 조회합니다
     */
    Optional<Teacher> findByIdAndCenterIsNull(Long userId);

    /**
     * 해당 user id을 가지고 해당 승인상태인 교사를 조회합니다
     */
    Optional<Teacher> findByIdAndApproval(Long userId, Approval approval);

    /**
     * 해당 시설과 시설 승인 여부로 교사를 조회합니다
     */
    List<Teacher> findByCenterAndApproval(Center center, Approval approval);

    /**
     * 해당 user id와 권한으로 교사를 조회합니다
     */
    Optional<Teacher> findByIdAndAuth(Long userId, Auth auth);

    /**
     * 해당 시설과 권한으로 교사를 조회합니다
     */
    List<Teacher> findByCenterAndAuth(Center center, Auth auth);

}
