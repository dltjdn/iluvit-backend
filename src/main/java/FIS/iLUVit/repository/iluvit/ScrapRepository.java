package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.domain.iluvit.Scrap;
import FIS.iLUVit.domain.iluvit.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {


    @Query("select distinct s " +
            "from Scrap s " +
            "left join fetch s.scrapPosts " +
            "where s.user.id =:userId")
    List<Scrap> findScrapsByUserWithScrapPosts(@Param("userId") Long userId);

    @Query("select s " +
            "from Scrap s " +
            "where s.id = :scrapId " +
            "and s.user.id = :userId")
    Optional<Scrap> findScrapByIdAndUserId(@Param("scrapId") Long scrapId, @Param("userId") Long userId);

    List<Scrap> findByUser(User user);

}
