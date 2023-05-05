package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.domain.iluvit.Board;
import FIS.iLUVit.domain.iluvit.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b where b.center.id = :centerId")
    List<Board> findByCenter(@Param("centerId") Long centerId);

    @Query("select b from Board b where b.center.id is null ")
    List<Board> findByCenterIsNull();

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


    @Query("select p from Post p join fetch p.board b where p.id in " +
            "(select max(p.id) from Post p where p.board.id in " +
            "(select b.id from Board b where b.center.id is null and b.isDefault = true) " +
            "group by p.board.id) and p.board.id = b.id")
    List<Post> findPostByDefault();
}
