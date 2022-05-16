package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.repository.dto.PresentationWithPtDatesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, Long> {

    @Query("select new FIS.iLUVit.repository.dto.PresentationWithPtDatesDto(p.id, p.startDate, p.endDate, p.place, p.content, p.imgCnt, p.videoCnt, pd.id, pd.date, pd.time, pd.ablePersonNum, pd.participantCnt, pd.waitingCnt) " +
            "from Presentation p " +
            "join p.ptDates as pd " +
            "where p.center.id =:centerId " +
            "and p.startDate <= :date " +
            "and :date <= p.endDate")
    List<PresentationWithPtDatesDto> findByCenterAndDateWithPtDates(@Param("centerId") Long centerId, @Param("date") LocalDate date);

}