package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.Child;
import FIS.iLUVit.domain.iluvit.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {
    @Query("select c " +
            "from Child c " +
            "join c.parent p " +
            "join c.center ct " +
            "where p.id = :userId " +
            "and ct.id = :centerId")
    List<Child> findByParentAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    @Query("select c " +
            "from Child c " +
            "left join fetch c.center " +
            "where c.id =:childId " +
            "and c.parent.id =:userId")
    Optional<Child> findByIdAndParentWithCenter(@Param("userId") Long userId, @Param("childId") Long childId);

    @Query("select c " +
            "from Child c " +
            "where c.id =:childId " +
            "and c.parent.id =:userId")
    Optional<Child> findByIdAndParent(@Param("userId") Long userId, @Param("childId") Long childId);

    @Query("select c " +
            "from Child c " +
            "left join fetch c.center " +
            "where c.parent.id =:userId")
    List<Child> findByUserWithCenter(@Param("userId") Long userId);

    List<Child> findByParent(Parent parent);

    List<Child> findByCenter(Center center);


}
