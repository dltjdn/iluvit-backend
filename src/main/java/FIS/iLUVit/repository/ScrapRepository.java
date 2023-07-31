package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.ScrapPost;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    /**
     * 헤딩 사용자로 스크랩 리스트를 조회합니다
     */
    List<Scrap> findByUser(User user);

    /**
     * 해당 scrapId와 사용자로 스크랩을 조회합니다
     */
    Optional<Scrap> findByIdAndUser(Long scrapId, User user);

}
