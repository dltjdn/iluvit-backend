package FIS.iLUVit.domain.child.repository;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.parent.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {

    /**
     * 부모로 아이 리스트를 조회합니다.
     */
    List<Child> findByParent(Parent parent);

    /**
     * 부모와 시설로 아이 리스트를 조회합니다
     */
    List<Child> findByParentAndCenter(Parent parent, Center center);

    /**
     * 아이 id와 부모로 아이를 조회한다
     */
    Optional<Child> findByIdAndParent(Long childId, Parent parent);

    /**
     * 시설로 아이 리스트를 조회합니다.
     */
    List<Child> findByCenter(Center center);

}
