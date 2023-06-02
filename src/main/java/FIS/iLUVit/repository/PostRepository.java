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

    /*
        게시글 id를 파라미터로 받아서 사용자와 게시판 및 시설에 ID로 조회합니다.
     */
    @Query("select p from Post p " +
            "left join fetch p.user u " +
            "left join fetch p.board b " +
            "left join fetch b.center c " +
            "where p.id = :postId")
    Optional<Post> findByIdWithUserAndBoardAndCenter(@Param("postId") Long postId);

    /*
        사용자 id를 파라미터로 받아서 사용자로 게시글을 조회합니다.
     */
    @Query(value = "select p from Post p join fetch p.user u join fetch p.board b where u.id = :userId")
    Slice<Post> findByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        게시판 id들로 게시글 top3를 조회합니다.
     */
    @Query(value = "select * from " +
            "(select row_number() over (partition by p.board_id order by p.created_date desc) as ranks, " +
            "p.* from post p where p.board_id in :boardIds) as ranking " +
            "where ranking.ranks <= 3 order by board_id, created_date desc ",
            nativeQuery = true)
    List<Post> findTop3(@Param("boardIds") List<Long> boardIds);

    /*
        게시판 id들로 게시글 top3_H2를 조회합니다.
     */
    @Query(value = "select * from " +
            "(select row_number() over (partition by p.board_id order by p.createddate desc) as ranks, " +
            "p.* from post p where p.board_id in :boardIds) as ranking " +
            "where ranking.ranks <= 3 order by board_id, createddate desc ",
            nativeQuery = true)
    List<Post> findTop3_H2(@Param("boardIds") List<Long> boardIds);

    /*
        하트 개수를 파라미터로 받아서 하트 개수로 top3를 조회합니다.
     */
    @Query("select p from Post p join p.board b " +
            "where b.center.id is null and p.heartCnt >= :heartCnt order by p.postCreateDate desc ")
    List<Post> findTop3ByHeartCnt(@Param("heartCnt") int heartCnt, Pageable pageable);

    /*
        하트 개수와 센터 id를 파라미터로 받아서 시설에 HeartCnt로 Top3를 조회합니다.
     */
    @Query("select p from Post p join p.board b " +
            "where b.center.id = :centerId and p.heartCnt >= :heartCnt order by p.postCreateDate desc ")
    List<Post> findTop3ByHeartCntWithCenter(@Param("heartCnt") int heartCnt, @Param("centerId") Long centerId,
                                            Pageable pageable);

    /*
        스크랩 id를 파라미터로 받아서 스크랩으로 게시글을 조회합니다.
     */
    @Query("select p " +
            "from Post p " +
            "join fetch p.scrapPosts sp " +
            "join fetch sp.scrap s " +
            "where s.id = :scrapId")
    Slice<Post> findByScrap(@Param("scrapId") Long scrapId);

    /*
        게시글 id를 파라미터로 받아서 보드에 ID로 게시글을 조회합니다.
     */
    @Query("select p " +
            "from Post p " +
            "join fetch p.board b " +
            "left join fetch b.center " +
            "where p.id = :postId")
    Optional<Post> findByIdWithBoard(@Param("postId") Long postId);
}
