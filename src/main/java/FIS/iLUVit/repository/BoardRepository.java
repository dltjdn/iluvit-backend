package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    /*
        시설 id별로 게시판 리스트를 조회합니다.
    */
    @Query("select b from Board b where b.center.id = :centerId")
    List<Board> findByCenterId(@Param("centerId") Long centerId);

    List<Board> findByCenter(Center center);

    /*
        시설 id가 널인 게시판 리스트를 조회합니다.
    */
    @Query("select b from Board b where b.center.id is null ")
    List<Board> findByCenterIsNull();

    /*
        게시판 id가 null인 게시판 이름별로 게시판을 조회합니다.
    */
    @Query("select b from Board b where b.center.id is null and b.name = :name")
    Optional<Board> findByName(@Param("name") String name);

    /*
        게시판 id와 게시판 이름별로 게시판을 조회합니다.
    */
    @Query("select b from Board b where b.center.id = :centerId and b.name = :name")
    Optional<Board> findByNameWithCenter(@Param("name") String name, @Param("centerId") Long centerId);

    /*
        게시판 기본값이 true인 시설 id별로 게시판 리스트를 조회합니다.
    */
    @Query("select b " +
            "from Board b " +
            "join fetch b.center c " +
            "where c.id = :centerId " +
            "and b.isDefault = true")
    List<Board> findDefaultByCenter(@Param("centerId") Long centerId);

    /*
        시설 id가 널이고 기본값이 true인 게시판 리스트를 조회합니다.
    */
    @Query("select b " +
            "from Board b " +
            "where b.center.id is null " +
            "and b.isDefault = true")
    List<Board> findDefaultByModu();

    /*
        게시글 id와 게시판 id의 기본값으로 게시글 리스트를 조회합니다.
    */
    @Query("select p from Post p join fetch p.board b where p.id in " +
            "(select max(p.id) from Post p where p.board.id in " +
            "(select b.id from Board b where b.center.id is null and b.isDefault = true) " +
            "group by p.board.id) and p.board.id = b.id")
    List<Post> findPostByDefault();
}
