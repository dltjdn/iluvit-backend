package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChildRepository extends JpaRepository<Child, Long> {
    @Query("select c from Child c join c.parent p join c.center ct where p.id = :userId and ct.id = :centerId")
    Child findByParentAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);
}
