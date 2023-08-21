package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    /*
        ScrapPosts의 사용자로 스크랩 리스트를 불러옵니다.
     */
    @Query("select distinct s " +
            "from Scrap s " +
            "left join fetch s.scrapPosts " +
            "where s.user.id = :userId")
    List<Scrap> findScrapsByUserWithScrapPosts(@Param("userId") Long userId);

    /*
        scrapId와 userId에 해당하는 스크랩을 조회합니다.
     */
    @Query("select s " +
            "from Scrap s " +
            "where s.id = :scrapId " +
            "and s.user.id = :userId")
    Optional<Scrap> findScrapByIdAndUserId(@Param("scrapId") Long scrapId, @Param("userId") Long userId);

    /*
        사용자로 스크랩 리스트를 조회합니다.
     */
    List<Scrap> findByUser(User user);

}
