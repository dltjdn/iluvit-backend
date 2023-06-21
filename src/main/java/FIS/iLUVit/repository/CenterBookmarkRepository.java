package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Prefer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CenterBookmarkRepository extends JpaRepository<Prefer, Long> {

    /*
        사용자 id와 시설 id를 파라미터로 받아서 사용자 ID 및 시설 Id로 조회합니다.
     */
    @Query("select p from Prefer p where p.parent.id =:userId and p.center.id =:centerId")
    Optional<Prefer> findByUserIdAndCenterId(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        부모로 조회합니다.
     */
    List<Prefer> findByParent(Parent parent);

    /*
        시설과 부모로 즐겨 찾기한 시설을 조회합니다.
     */
    @Query("select prefer from Prefer prefer where prefer.center.id = :centerId and prefer.parent.id = :userId")
    Optional<Prefer> findByCenterAndParent(@Param("centerId") Long centerId, @Param("userId") Long userId);


}
