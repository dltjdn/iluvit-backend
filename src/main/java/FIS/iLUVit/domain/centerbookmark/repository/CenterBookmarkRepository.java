package FIS.iLUVit.domain.centerbookmark.repository;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.centerbookmark.domain.Prefer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CenterBookmarkRepository extends JpaRepository<Prefer, Long> {

    /**
     * 해당 시설의 시설 즐겨찾기 리스트를 조회합니다
     */
    List<Prefer> findByCenter(Center center);

    /**
     * 해당 부모의 시설 즐겨찾기 리스트를 조회합니다
     */
    List<Prefer> findByParent(Parent parent);

    /**
       해당 부모의 해당 시설에 대한 즐겨찾기가 있는지 조회합니다
    */
    Optional<Prefer> findByCenterAndParent(Center center, Parent parent);

}
