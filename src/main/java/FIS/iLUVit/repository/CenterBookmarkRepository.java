package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Prefer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CenterBookmarkRepository extends JpaRepository<Prefer, Long> {

    /**
     * 해당 시설로 시설 즐겨찾기 리스트를 조회합니다
     */
    List<Prefer> findByCenter(Center center);

    /*
        부모 id와 시설 id별로 센터 즐겨찾기를 조회합니다.
     */
    @Query("select p from Prefer p where p.parent.id =:userId and p.center.id =:centerId")
    Optional<Prefer> findByUserIdAndCenterId(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        부모 id별로 센터 즐겨찾기를 조회합니다.
     */
    List<Prefer> findByParent(Parent parent);

    /*
        유저의 해당 센터 북마크를 조회한다.
    */
    //TODO 확인해보기
    Optional<Prefer> findByCenterAndParent(Center center, Parent parent);

}
