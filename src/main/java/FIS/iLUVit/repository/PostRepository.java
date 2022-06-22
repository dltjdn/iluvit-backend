package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    @Query("select p from Post p left join fetch p.user u left join fetch p.board b " +
            "left join fetch b.center c where p.id = :postId")
    Optional<Post> findByIdWithUserAndBoardAndCenter(@Param("postId") Long postId);

    @Query(value = "select p from Post p join fetch p.user u join fetch p.board b where u.id = :userId",
            countQuery = "select count(p) from Post p where p.user.id = :userId")
    Slice<Post> findByUser(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * from " +
            "(select row_number() over (partition by p.board_id order by p.createddate desc) as rank, " +
            "p.* from Post p where p.board_id in :boardIds) as ranking " +
            "where ranking.rank <= 4 order by board_id, createdDate desc ",
            nativeQuery = true)
    List<Post> findTop4(@Param("boardIds") List<Long> boardIds);

    @Query("select p " +
            "from Post p " +
            "join fetch p.scrapPosts sp " +
            "join fetch sp.scrap s " +
            "where s.id = :scrapId")
    Slice<Post> findByScrap(@Param("scrapId") Long scrapId);
}
