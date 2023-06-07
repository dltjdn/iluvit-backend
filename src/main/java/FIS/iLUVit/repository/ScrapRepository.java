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
        사용자 id를 파라미터로 받아서 게시글들 스크랩한 사용자로 게시글들을 조회합니다.
     */
    @Query("select distinct s " +
            "from Scrap s " +
            "left join fetch s.scrapPosts " +
            "where s.user.id =:userId")
    List<Scrap> findScrapsByUserWithScrapPosts(@Param("userId") Long userId);

    /*
        스크랩 id와 사용자 id를 파라미터로 받아서 id 및 사용자 id로 스크랩을 조회합니다.
     */
    @Query("select s " +
            "from Scrap s " +
            "where s.id = :scrapId " +
            "and s.user.id = :userId")
    Optional<Scrap> findScrapByIdAndUserId(@Param("scrapId") Long scrapId, @Param("userId") Long userId);

    /*
        사용자로 스크랩을 조회합니다.
     */
    List<Scrap> findByUser(User user);

}
