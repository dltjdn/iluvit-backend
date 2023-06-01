package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    /*
        센터 id를 파라미터로 받아서 게시판에 있는 시설을 조회합니다.
     */
    @Query("select b from Board b where b.center.id = :centerId")
    List<Board> findByCenter(@Param("centerId") Long centerId);

    /*
        게시판에서 시설 id가 null인 것을 조회합니다.
     */
    @Query("select b from Board b where b.center.id is null ")
    List<Board> findByCenterIsNull();

    /*
        게시판에 있는 시설 id가 null인 것을 이름으로 조회합니다.
     */
    @Query("select b from Board b where b.center.id is null and b.name = :name")
    Optional<Board> findByName(@Param("name") String name);

    /*
        이름과 시설 id를 파라미터로 받아서 시설과 함께 이름으로 조회합니다.
     */
    @Query("select b from Board b where b.center.id = :centerId and b.name = :name")
    Optional<Board> findByNameWithCenter(@Param("name") String name, @Param("centerId") Long centerId);

    /*
        시설 id를 파라미터로 받아서 시설로 기본값을 조회합니다.
     */
    @Query("select b " +
            "from Board b " +
            "join fetch b.center c " +
            "where c.id =:centerId " +
            "and b.isDefault = true")
    List<Board> findDefaultByCenter(@Param("centerId") Long centerId);

    /*
        Modu로 기본값을 조회합니다.
     */
    @Query("select b " +
            "from Board b " +
            "where b.center.id is null " +
            "and b.isDefault = true")
    List<Board> findDefaultByModu();

    /*
        기본값으로 게시글을 조회합니다.
     */
    @Query("select p from Post p join fetch p.board b where p.id in " +
            "(select max(p.id) from Post p where p.board.id in " +
            "(select b.id from Board b where b.center.id is null and b.isDefault = true) " +
            "group by p.board.id) and p.board.id = b.id")
    List<Post> findPostByDefault();
}
