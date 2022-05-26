package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p left join fetch p.user u left join fetch p.board b " +
            "left join fetch b.center c where p.id = :postId")
    Optional<Post> findByIdWithUserAndBoardAndCenter(@Param("postId") Long postId);

    @Query("select p from Post p where p.title like %:keyword% or p.content like %:keyword%")
    List<Post> findByKeyword(@Param("keyword") String keyword);

    @Query("select p from Post p left join fetch p.board b join fetch b.center c where c.id = :centerId" +
            " and (p.title like %:keyword% or p.content like %:keyword%)")
    List<Post> findByKeywordAndCenter(@Param("centerId") Long centerId, @Param("keyword") String keyword);

    @Query("select p from Post p left join fetch p.board b where b.id = :boardId" +
            " and (p.title like %:keyword% or p.content like %:keyword%)")
    List<Post> findByKeywordAndBoard(@Param("boardId") Long boardId, @Param("keyword") String keyword);

    @Query("select p from Post p left join fetch p.board b where b.id in :boardIds")
    List<Post> findAllWithBoardIds(@Param("boardIds") List<Long> boardIds);

    @Query("select p from Post p left join fetch p.board b where b.id in :boardIds" +
            " and (p.title like %:keyword% or p.content like %:keyword%)")
    List<Post> findByKeywordWithBoardIds(@Param("keyword") String keyword, @Param("boardIds") List<Long> boardIds);
}
