package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Bookmark;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardBookmarkRepository extends JpaRepository<Bookmark, Long> {

    /*
        유저 id를 파라미터로 받아서 사용자 및 게시판으로 북마크한 게시판을 조회합니다.
    */
    @Query("select b from Bookmark b join fetch b.user u join fetch b.board bd left join fetch bd.center c " +
            "where u.id = :userId")
    List<Bookmark> findWithUserAndBoard(@Param("userId") Long userId);

    /*
        유저 id를 파라미터로 받아서 사용자 및 게시판 센터가 null인 것으로 북마크한 게시판을 조회합니다.
    */
    @Query("select b from Bookmark b join fetch b.user u join fetch b.board bd left outer join bd.center c " +
            "where u.id = :userId and c.id is null ")
    List<Bookmark> findWithUserAndBoardCenterIsNull(@Param("userId") Long userId);

    /*
        유저 id를 파라미터로 받아서 게시판별 게시글을 조회합니다.
    */
    @Query("select p from Post p join fetch p.board b where p.id in " +
            "(select max(p.id) from Post p where p.board.id in " +
            "(select b.board.id from Bookmark b where b.user.id = :userId) " +
            "group by p.board.id) and p.board.id = b.id")
    List<Post> findPostByBoard(@Param("userId") Long userId);

    /*
        유저 id를 파라미터로 받아서 사용자별 게시판을 조회합니다.
    */
    @Query("select bm from Bookmark bm join fetch bm.board b where bm.user.id = :userId and b.center.id is null ")
    List<Bookmark> findBoardByUser(@Param("userId") Long userId);

    /*
        유저 id와 센터 id를 파라미터로 받아서 사용자 및 센터별 게시판을 조회합니다.
    */
    @Query("select bm from Bookmark bm join fetch bm.board b where bm.user.id = :userId and b.center.id = :centerId ")
    List<Bookmark> findBoardByUserAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        유저 id와 여러 개의 게시판 id를 파라미터로 받아서 게시판 및 사용자별로 모두 삭제합니다.
    */
    @Modifying
    @Query("delete " +
            "from Bookmark b " +
            "where b.user.id =:userId " +
            "and b.board.id in :boardIds")
    void deleteAllByBoardAndUser(@Param("userId") Long userId, @Param("boardIds") List<Long> boardIds);

    /*
        유저 id와 센터 id를 파라미터로 받아서 센터 및 사용자별로 모두 삭제합니다.
    */
    @Modifying
    @Query("delete " +
            "from Bookmark b " +
            "where b.user.id =:userId " +
            "and b.id in " +
            "(select b.id " +
            "from Bookmark b " +
            "join b.board bb " +
            "where bb.center.id =:centerId)")
    void deleteAllByCenterAndUser(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        사용자를 조회합니다.
    */
    List<Bookmark> findByUser(User user);

    /*
        사용자 id를 파라미터로 받아서 게시판 및 센터를 사용하여 사용자별로 조회합니다.
    */
    @Query("select bm from Bookmark bm " +
            "join fetch bm.board b " +
            "left join fetch b.center c " +
            "where bm.user.id = :userId")
    List<Bookmark> findByUserWithBoardAndCenter(@Param("userId") Long userId);
}
