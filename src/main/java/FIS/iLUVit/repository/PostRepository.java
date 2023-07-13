package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findByBoard(Board defaultBoard, Sort id);

    /*
        사용자, 게시판, 시설 id로 게시글을 조회합니다.
     */
    @Query("select p from Post p " +
            "left join fetch p.user u " +
            "left join fetch p.board b " +
            "left join fetch b.center c " +
            "where p.id = :postId")
    Optional<Post> findByIdWithUserAndBoardAndCenter(@Param("postId") Long postId);

    /*
        사용자 id로 게시글을 조회합니다.
     */
    @Query(value = "select p from Post p join fetch p.user u join fetch p.board b where u.id = :userId")
    Slice<Post> findByUser(@Param("userId") Long userId, Pageable pageable);

    /*
        게시판 id 리스트 중 최근 3개의 게시글을 불러옵니다.
     */
    @Query(value = "select * from " +
            "(select row_number() over (partition by p.board_id order by p.created_date desc) as ranks, " +
            "p.* from post p where p.board_id in :boardIds) as ranking " +
            "where ranking.ranks <= 3 order by board_id, created_date desc ",
            nativeQuery = true)
    List<Post> findTop3(@Param("boardIds") List<Long> boardIds);


    /*
        게시글 하트 개수가 가장 많은 3개의 게시글 리스트를 불러옵니다.
     */
    @Query("select p from Post p join p.board b " +
            "where b.center.id is null and p.heartCnt >= :heartCnt order by p.postCreateDate desc ")
    List<Post> findTop3ByHeartCnt(@Param("heartCnt") int heartCnt, Pageable pageable);

    /*
        시설에 있는 게시글중 하트가 가장 많은 3개의 게시글 리스트를 불러옵니다.
     */
    @Query("select p from Post p join p.board b " +
            "where b.center.id = :centerId and p.heartCnt >= :heartCnt order by p.postCreateDate desc ")
    List<Post> findTop3ByHeartCntWithCenter(@Param("heartCnt") int heartCnt, @Param("centerId") Long centerId,
                                            Pageable pageable);


    /*
        게시판에 있는 게시글 id로 게시글을 불러옵니다.
     */
    @Query("select p " +
            "from Post p " +
            "join fetch p.board b " +
            "left join fetch b.center " +
            "where p.id = :postId")
    Optional<Post> findByIdWithBoard(@Param("postId") Long postId);
}
