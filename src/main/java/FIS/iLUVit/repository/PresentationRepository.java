package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.repository.dto.PresentationPreviewForTeacher;
import FIS.iLUVit.repository.dto.PresentationWithPtDatesDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, Long>, PresentationRepositoryCustom {

    @Query("select new FIS.iLUVit.repository.dto.PresentationWithPtDatesDto" +
            "(p.id, p.startDate, p.endDate, p.place, p.content, p.imgCnt, p.videoCnt, pd.id, pd.date, pd.time, pd.ablePersonNum, pd.participantCnt, pd.waitingCnt) " +
            "from Presentation p " +
            "join p.ptDates as pd " +
            "where p.center.id =:centerId " +
            "and :date <= p.endDate")
    List<PresentationWithPtDatesDto> findByCenterAndDateWithPtDates(@Param("centerId") Long centerId, @Param("date") LocalDate date);

    @Query("select new FIS.iLUVit.repository.dto.PresentationWithPtDatesDto" +
            "(p.id, p.startDate, p.endDate, p.place, p.content, p.imgCnt, p.videoCnt, pd.id, pd.date, pd.time, pd.ablePersonNum, pd.participantCnt, pd.waitingCnt, participation.id ,waiting.id) " +
            "from Presentation p " +
            "join p.ptDates as pd " +
            "left join pd.participations as participation on participation.parent.id =:userId and participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED " +
            "left join pd.waitings as waiting on waiting.parent.id =:userId " +
            "where p.center.id =:centerId " +
            "and p.startDate <= :date " +
            "and :date <= p.endDate")
    List<PresentationWithPtDatesDto> findByCenterAndDateWithPtDates(@Param("centerId") Long centerId, @Param("date") LocalDate date, @Param("userId") Long userId);

    @Query("select new FIS.iLUVit.repository.dto.PresentationPreviewForTeacher(p.id, p.startDate, p.endDate, p.place, p.content) " +
            "from Presentation p " +
            "where p.center.id = :centerId")
    List<PresentationPreviewForTeacher> findByCenterId(@Param("centerId") Long centerId);

    @Query("select distinct presentation from Presentation presentation " +
            "join fetch presentation.ptDates " +
            "join fetch presentation.center " +
            "where presentation.id = :presentationId")
    Optional<Presentation> findByIdAndJoinPtDate(@Param("presentationId") Long presentationId);

    @Query("select presentation from Presentation presentation " +
            "where presentation.endDate >= :date " +
            "and presentation.center.id = :centerId")
    Presentation findByCenterIdAndDate(@Param("centerId") Long centerId, @Param("date") LocalDate date);
}