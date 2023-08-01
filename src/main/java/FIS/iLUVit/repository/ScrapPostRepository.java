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
    Slice<ScrapPost> findByScrap_IdAndScrap_User(Long scrapId, User user, Pageable pageable);

    /**
     * 해당 사용자 id와 스크랩 게시글 id로 스크랩된 게시글을 조회합니다
     */
    Optional<ScrapPost> findByIdAndScrap_User(Long scrapPostId, User user);

}
