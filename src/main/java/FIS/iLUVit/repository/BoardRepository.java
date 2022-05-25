package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b left join b.center c where (c.id = :centerId) or c.id is null")
    List<Board> findByUserWithCenter(@Param("centerId") Long centerId);

    @Query("select b from Board b left join b.center c where (c.id in :centerIds) or c.id is null")
    List<Board> findByUserWithCenterIds(@Param("centerIds") List<Long> centerIds);
}
