package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    @Query("select ct from Child c join c.center ct join c.parent p where p.id = :userId")
    List<Center> findByUser(@Param("userId") Long userId);

    @Query("select distinct c " +
            "from Center c " +
            "left join fetch c.teachers " +
            "where c.id =:centerId")
    Optional<Center> findByIdWithTeacher(@Param("centerId") Long centerId);

    @Query("select distinct c " +
            "from Center c " +
            "join fetch c.teachers t " +
            "where c.id =:centerId " +
            "and t.approval = 'ACCEPT' " +
            "and c.signed = true")
    Optional<Center> findByIdAndSignedWithTeacher(@Param("centerId") Long center_id);


}