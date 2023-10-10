package FIS.iLUVit.domain.ptdate.repository;

import FIS.iLUVit.domain.presentation.domain.Presentation;
import FIS.iLUVit.domain.ptdate.domain.PtDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PtDateRepository extends JpaRepository<PtDate, Long> {

    List<PtDate> findByPresentation(Presentation presentation);
    void deleteByIdIn(List<Long> ptDateIds);

}
