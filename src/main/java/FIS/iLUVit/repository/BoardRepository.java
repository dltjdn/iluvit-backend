package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b where b.center.id = :centerId")
    List<Board> findByUserWithCenter(@Param("centerId") Long centerId);

    @Query("select b from Board b left join b.center c where (c.id in :centerIds) or c.id is null")
    List<Board> findByUserWithCenterIds(@Param("centerIds") Set<Long> centerIds);

    @Query("select b from Board b where b.center.id = :centerId")
    List<Board> findByCenter(@Param("centerId") Long centerId);

    List<Board> findByCenterIsNull();

    @Query("select b from Board b where b.center.id is null ")
    List<Board> findByUserWithCenterIsNull();

    @Query("select b from Board b where b.center.id is null and b.name = :name")
    Optional<Board> findByName(@Param("name") String name);

    @Query("select b from Board b where b.center.id = :centerId and b.name = :name")
    Optional<Board> findByNameWithCenter(@Param("name") String name, @Param("centerId") Long centerId);

    @Query("select b " +
            "from Board b " +
            "join fetch b.center c " +
            "where c.id =:centerId " +
            "and b.isDefault = true")
    List<Board> findDefaultByCenter(@Param("centerId") Long centerId);

    @Query("select b " +
            "from Board b " +
            "where b.center.id is null " +
            "and b.isDefault = true")
    List<Board> findDefaultByModu();
}
