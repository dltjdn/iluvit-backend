package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Bookmark;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardBookmarkRepository extends JpaRepository<Bookmark, Long> {

    /*
        사용자 id별로 즐겨찾기 리스트를 조회합니다.
    */
    @Query("select b from Bookmark b join fetch b.user u join fetch b.board bd left join fetch bd.center c " +
            "where u.id = :userId")
    List<Bookmark> findWithUserAndBoard(@Param("userId") Long userId);

    /*
        센터 id가 null이 아닌 사용자 id별로 즐겨찾기 리스트를 조회합니다.
    */
    @Query("select b from Bookmark b join fetch b.user u join fetch b.board bd left outer join bd.center c " +
            "where u.id = :userId and c.id is null ")
    List<Bookmark> findWithUserAndBoardCenterIsNull(@Param("userId") Long userId);

    /*
        게시판 id로 즐겨찾기 게시글 리스트를 조회합니다.
    */
    @Query("select p from Post p join fetch p.board b where p.id in " +
            "(select max(p.id) from Post p where p.board.id in " +
            "(select b.board.id from Bookmark b where b.user.id = :userId) " +
            "group by p.board.id) and p.board.id = b.id and p.user.id not in :blockedUserIds ")
    List<Post> findPostByBoard(@Param("userId") Long userId, @Param("blockedUserIds") List<Long> blockedUserIds);
    @Query("select p from Post p join fetch p.board b where p.id in " +
            "(select max(p.id) from Post p where p.board.id in " +
            "(select b.board.id from Bookmark b where b.user.id = :userId) " +
            "group by p.board.id) and p.board.id = b.id ")
    List<Post> findPostByBoard(@Param("userId") Long userId);

    /*
        시설에 가입되어 있지 않은 사용자별 즐겨찾기 리스트를 조회합니다.
    */
    @Query("select bm from Bookmark bm join fetch bm.board b where bm.user.id = :userId and b.center.id is null ")
    List<Bookmark> findBoardByUser(@Param("userId") Long userId);

    /*
        사용자 및 게시판별로 즐겨찾기를 조회합니다.
    */
    @Query("select bm from Bookmark bm where bm.user.id = :userId and bm.board.id = :boardId")
    Optional<Bookmark> findBoardBookmarkByUserAndBoard(@Param("userId") Long userId, @Param("boardId") Long boardId);

    /*
        시설 및 사용자별로 즐겨찾기 리스트를 조회합니다.
    */
    @Query("select bm from Bookmark bm join fetch bm.board b where bm.user.id = :userId and b.center.id = :centerId ")
    List<Bookmark> findBoardByUserAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    /*
        사용자 id와 게시판 id 리스트별로 즐겨찾기를 삭제합니다.
    */
    @Modifying
    @Query("delete " +
            "from Bookmark b " +
            "where b.user.id =:userId " +
            "and b.board.id in :boardIds")
    void deleteAllByBoardAndUser(@Param("userId") Long userId, @Param("boardIds") List<Long> boardIds);

    /*
        사용자 id와 시설 id별로 즐겨찾기를 삭제합니다.
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
        사용자별로 즐겨찾기 리스트를 조회합니다.
    */
    List<Bookmark> findByUser(User user);

    /*
        즐겨찾기 사용자 id별로 즐겨찾기 리스트를 조회합니다.
    */
    @Query("select bm from Bookmark bm " +
            "join fetch bm.board b " +
            "left join fetch b.center c " +
            "where bm.user.id = :userId")
    List<Bookmark> findByUserWithBoardAndCenter(@Param("userId") Long userId);
}
