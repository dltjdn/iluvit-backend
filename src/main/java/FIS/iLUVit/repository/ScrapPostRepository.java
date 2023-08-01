package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ScrapPost;
import FIS.iLUVit.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScrapPostRepository extends JpaRepository<ScrapPost, Long> {

    /**
     * 해당 사용자 id와 스크랩 폴더 id로 스크랩된 게시글을 조회합니다
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

    Slice<ScrapPost> findByScrap_IdAndScrap_User(Long scrapId, User user, Pageable pageable);

    /**
     * 해당 사용자 id와 스크랩 게시글 id로 스크랩된 게시글을 조회합니다
     */
    @Query("select sp " +
            "from ScrapPost sp " +
            "join sp.scrap s " +
            "where sp.id =:scrapPostId " +
            "and s.user.id =:userId")
    Optional<ScrapPost> findByScrapAndPost(@Param("userId") Long userId, @Param("scrapPostId") Long scrapPostId);

    Optional<ScrapPost> findByIdAndScrap_User(Long scrapPostId, User user);
}
