package FIS.iLUVit.repository;

import FIS.iLUVit.domain.ScrapPost;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScrapPostRepository extends JpaRepository<ScrapPost, Long> {

    @Query("select sp " +
            "from ScrapPost sp " +
            "join fetch sp.post p " +
            "join fetch p.user u " +
            "join fetch p.board " +
            "where sp.scrap.id =:scrapId")
    Slice<ScrapPost> findByScrapWithPost(@Param("scrapId") Long scrapId);
}
