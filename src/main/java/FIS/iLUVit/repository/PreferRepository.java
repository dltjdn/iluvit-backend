package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Prefer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PreferRepository extends JpaRepository<Prefer, Long> {

    @Query("select p from Prefer p where p.parent.id =:userId and p.center.id =:centerId")
    Optional<Prefer> findByUserIdAndCenterId(@Param("userId") Long userId, @Param("centerId") Long centerId);

}
