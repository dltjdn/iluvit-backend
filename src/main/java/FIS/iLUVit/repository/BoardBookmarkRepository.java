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
    @Query("select b from Bookmark b join fetch b.user u join fetch b.board bd left join fetch bd.center c " +
            "where u.id = :userId")
    List<Bookmark> findWithUserAndBoard(@Param("userId") Long userId);

    @Query("select b from Bookmark b join fetch b.user u join fetch b.board bd left outer join bd.center c " +
            "where u.id = :userId and c.id is null ")
    List<Bookmark> findWithUserAndBoardCenterIsNull(@Param("userId") Long userId);

    @Query("select p from Post p join fetch p.board b where p.id in " +
            "(select max(p.id) from Post p where p.board.id in " +
            "(select b.board.id from Bookmark b where b.user.id = :userId) " +
            "group by p.board.id) and p.board.id = b.id")
    List<Post> findPostByBoard(@Param("userId") Long userId);

    @Query("select bm from Bookmark bm join fetch bm.board b where bm.user.id = :userId and b.center.id is null ")
    List<Bookmark> findBoardByUser(@Param("userId") Long userId);

    @Query("select bm from Bookmark bm where bm.user.id = :userId and bm.board.id = :boardId")
    Optional<Bookmark> findBoardBookmarkByUserAndBoard(@Param("userId") Long userId, @Param("boardId") Long boardId);

    @Query("select bm from Bookmark bm join fetch bm.board b where bm.user.id = :userId and b.center.id = :centerId ")
    List<Bookmark> findBoardByUserAndCenter(@Param("userId") Long userId, @Param("centerId") Long centerId);

    @Modifying
    @Query("delete " +
            "from Bookmark b " +
            "where b.user.id =:userId " +
            "and b.board.id in :boardIds")
    void deleteAllByBoardAndUser(@Param("userId") Long userId, @Param("boardIds") List<Long> boardIds);

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


    List<Bookmark> findByUser(User user);

    @Query("select bm from Bookmark bm " +
            "join fetch bm.board b " +
            "left join fetch b.center c " +
            "where bm.user.id = :userId")
    List<Bookmark> findByUserWithBoardAndCenter(@Param("userId") Long userId);
}
