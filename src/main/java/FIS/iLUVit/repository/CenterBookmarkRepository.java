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

    @Query("select p from Prefer p where p.parent.id =:userId and p.center.id =:centerId")
    Optional<Prefer> findByUserIdAndCenterId(@Param("userId") Long userId, @Param("centerId") Long centerId);

    List<Prefer> findByParent(Parent parent);

    @Query("select prefer from Prefer prefer where prefer.center.id = :centerId and prefer.parent.id = :userId")
    Optional<Prefer> findByCenterAndParent(@Param("centerId") Long centerId, @Param("userId") Long userId);
}
