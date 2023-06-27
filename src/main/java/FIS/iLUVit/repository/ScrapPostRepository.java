package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ScrapPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScrapPostRepository extends JpaRepository<ScrapPost, Long> {

    /*
        사용자 ID와 스크랩 ID에 해당하는 스크랩된 게시글을 조회합니다.
     */
    @Query("select sp " +
            "from ScrapPost sp " +
            "join fetch sp.scrap s " +
            "join fetch sp.post p " +
            "join fetch p.user u " +
            "join fetch p.board " +
            "where s.id =:scrapId " +
            "and s.user.id =:userId")
    Slice<ScrapPost> findByScrapWithPost(@Param("userId") Long userId, @Param("scrapId") Long scrapId, Pageable pageable);

    /*
        사용자 ID와 스크랩 게시글 ID에 해당하는 스크랩된 게시글을 조회합니다.
     */
    @Query("select sp " +
            "from ScrapPost sp " +
            "join sp.scrap s " +
            "where sp.id =:scrapPostId " +
            "and s.user.id =:userId")
    Optional<ScrapPost> findByScrapAndPost(@Param("userId") Long userId, @Param("scrapPostId") Long scrapPostId);
}
