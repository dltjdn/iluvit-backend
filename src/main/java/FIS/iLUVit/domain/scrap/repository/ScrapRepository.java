package FIS.iLUVit.domain.scrap.repository;

import FIS.iLUVit.domain.scrap.domain.Scrap;
import FIS.iLUVit.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

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