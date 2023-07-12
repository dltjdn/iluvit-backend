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

    /**
     * 해당 시설의 게시판 리스트를 조회합니다
     */
    List<Board> findByCenter(Center center);


//    @Query("select b from Board b where b.center.id is null ")
//    List<Board> findByCenterIsNull();
    /**
     * 시설에 속해있지 않은 게시판 리스트를 조회합니다
     */
    List<Board> findByCenterIsNull();

//    @Query("select b from Board b where b.center.id is null and b.name = :name")
//    Optional<Board> findByName(@Param("name") String name);
    /**
     * 시설에 속해 있지 않은 게시판 중 해당 이름과 일치하는 게시판을 조회합니다
     */
    Optional<Board> findByCenterIsNullAndName(String name);

//    @Query("select b from Board b where b.center.id = :centerId and b.name = :name")
//    Optional<Board> findByNameWithCenter(@Param("name") String name, @Param("centerId") Long centerId);
    /**
     * 해당 시설에서 해당 이름을 가진 게시판을 조회합니다
     */
    Optional<Board> findByCenterAndName(Center center, String name);


//    @Query("select b " +
//            "from Board b " +
//            "join fetch b.center c " +
//            "where c.id =:centerId " +
//            "and b.isDefault = true")
//    List<Board> findDefaultByCenter(@Param("centerId") Long centerId);

    /**
     * 해당 시설에서 디폴트인 게시판 리스트를 조회합니다
     */
    List<Board> findByCenterAndIsDefaultTrue(Center center);

//    @Query("select b " +
//            "from Board b " +
//            "where b.center.id is null " +
//            "and b.isDefault = true")
//    List<Board> findDefaultByModu();

    /**
     * 시설에 속해있지 않은 게시판 중 디폴트인 게시판 리스트를 조회합니다
     */
    List<Board> findByCenterIsNullAndIsDefaultTrue();

//    /*
//        게시글 id와 게시판 id의 기본값으로 게시글 리스트를 조회합니다.
//    */
//    @Query("select p from Post p join fetch p.board b where p.id in " +
//            "(select max(p.id) from Post p where p.board.id in " +
//            "(select b.id from Board b where b.center.id is null and b.isDefault = true) " +
//            "group by p.board.id) and p.board.id = b.id")
//    List<Post> findPostByDefault();
}
