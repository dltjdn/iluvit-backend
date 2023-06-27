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
        부모 id와 시설 id별로 센터 즐겨찾기를 조회합니다.
     */
    @Query("select p from Prefer p where p.parent.id =:userId and p.center.id =:centerId")
    Optional<Prefer> findByUserIdAndCenterId(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        부모 id별로 센터 즐겨찾기를 조회합니다.
     */
    List<Prefer> findByParent(Parent parent);
}
