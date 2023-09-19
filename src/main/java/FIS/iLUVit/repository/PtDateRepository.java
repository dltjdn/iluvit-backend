package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PtDateRepository extends JpaRepository<PtDate, Long> {

    /*
        ptDateKeysDeleteTarget에 해당하는 id를 가진 PtDate들을 삭제합니다.
     */
    @Modifying
    @Query("delete from PtDate ptdate where ptdate.id in :ptDateKeys")
    void deletePtDateByIds(@Param("ptDateKeys") Set<Long> ptDateKeysDeleteTarget);

    List<PtDate> findByPresentation(Presentation presentation);

}
