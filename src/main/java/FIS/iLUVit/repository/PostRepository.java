package FIS.iLUVit.repository;

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

    @Query(value = "select p from Post p join fetch p.user u join fetch p.board b where u.id = :userId")
    Slice<Post> findByUser(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * from " +
            "(select row_number() over (partition by p.board_id order by p.createddate desc) as ranks, " +
            "p.* from post p where p.board_id in :boardIds) as ranking " +
            "where ranking.ranks <= 4 order by board_id, createddate desc ",
            nativeQuery = true)
    List<Post> findTop4(@Param("boardIds") List<Long> boardIds);

    @Query("select p from Post p join p.board b " +
            "where b.center.id is null and p.heartCnt >= :heartCnt order by p.postCreateDate desc ")
    List<Post> findByHeartCnt(@Param("heartCnt") int heartCnt, Pageable pageable);

    @Query("select p from Post p join p.board b " +
            "where b.center.id = :centerId and p.heartCnt >= :heartCnt order by p.postCreateDate desc ")
    List<Post> findByHeartCntWithCenter(@Param("heartCnt") int heartCnt, @Param("centerId") Long centerId,
                                        Pageable pageable);

    @Query("select p " +
            "from Post p " +
            "join fetch p.scrapPosts sp " +
            "join fetch sp.scrap s " +
            "where s.id = :scrapId")
    Slice<Post> findByScrap(@Param("scrapId") Long scrapId);
}
