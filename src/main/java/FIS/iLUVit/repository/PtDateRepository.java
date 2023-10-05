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

    List<PtDate> findByPresentation(Presentation presentation);
    void deleteByIdIn(List<Long> ptDateIds);

}
