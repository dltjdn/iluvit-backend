package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    @Query("select distinct s from Scrap s left join fetch s.scrapPosts where s.user.id = :userId")
    List<Scrap> findScrapsWithScrapPostsByUser(@Param("userId") Long userId);

}
