package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    /*
        사용자 id를 파라미터로 받아서 사용자로 조회합니다.
     */
    @Query("select ct " +
            "from Child c " +
            "join c.center ct" +
            " join c.parent p " +
            "where p.id = :userId")
    List<Center> findByUser(@Param("userId") Long userId);

    /*
        시설 Id로 조회합니다.
     */
    Optional<Center> findById(Long centerId);

    /*
        시설 Id를 파라미터로 받아서 교사의 서명과 id로 조회합니다.
     */
    @Query("select c " +
            "from Teacher t " +
            "join t.center c " +
            "where c.id =:centerId " +
            "and t.approval = 'ACCEPT' " +
            "and c.signed = true")
    Optional<Center> findByIdAndSignedWithTeacher(@Param("centerId") Long center_id);


}