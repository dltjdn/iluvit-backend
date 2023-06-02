package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.dto.presentation.PresentationForTeacherDto;
import FIS.iLUVit.dto.presentation.PresentationWithPtDatesDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, Long>, PresentationRepositoryCustom {

    /*
        시설 id와 날짜를 파라미터로 받아서 설명회 날짜가 있는 센터 및 날짜로 조회합니다.
     */
    @Query("select new FIS.iLUVit.dto.presentation.PresentationWithPtDatesDto" +
            "(p.id, p.startDate, p.endDate, p.place, p.content, p.imgCnt, p.videoCnt, p.infoImagePath, pd.id, pd.date, pd.time, pd.ablePersonNum, pd.participantCnt, pd.waitingCnt) " +
            "from Presentation p " +
            "join p.ptDates as pd " +
            "where p.center.id =:centerId " +
            "and :date <= p.endDate")
    List<PresentationWithPtDatesDto> findByCenterAndDateWithPtDates(@Param("centerId") Long centerId, @Param("date") LocalDate date);

    /*
        시설 id와 날짜와 사용자 id를 파라미터로 받아서 설명회 날짜가 있는 시설 및 날짜로 조회합니다.
     */
    @Query("select new FIS.iLUVit.dto.presentation.PresentationWithPtDatesDto" +
            "(p.id, p.startDate, p.endDate, p.place, p.content, p.imgCnt, p.videoCnt, p.infoImagePath, pd.id, pd.date, pd.time, pd.ablePersonNum, pd.participantCnt, pd.waitingCnt, participation.id ,waiting.id) " +
            "from Presentation p " +
            "join p.ptDates as pd " +
            "left join pd.participations as participation on participation.parent.id =:userId and participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED " +
            "left join pd.waitings as waiting on waiting.parent.id =:userId " +
            "where p.center.id =:centerId " +
            "and p.startDate <= :date " +
            "and :date <= p.endDate")
    List<PresentationWithPtDatesDto> findByCenterAndDateWithPtDates(@Param("centerId") Long centerId, @Param("date") LocalDate date, @Param("userId") Long userId);

    /*
        시설 id를 파라미터로 받아서 시설 id로 조회합니다.
     */
    @Query("select new FIS.iLUVit.dto.presentation.PresentationForTeacherDto(p.id, p.startDate, p.endDate, p.place, p.content, p.infoImagePath) " +
            "from Presentation p " +
            "where p.center.id = :centerId")
    List<PresentationForTeacherDto> findByCenterId(@Param("centerId") Long centerId, Pageable pageable);

    /*
        설명회 id를 파라미터로 받아서 ID 및 설명회 가입 날짜로 조회합니다.
     */
    @Query("select distinct presentation from Presentation presentation " +
            "join fetch presentation.ptDates " +
            "join fetch presentation.center " +
            "where presentation.id = :presentationId")
    Optional<Presentation> findByIdAndJoinPtDate(@Param("presentationId") Long presentationId);

    /*
        시설 id와 날짜를 파라미터로 받아서 시설 ID 및 날짜로 조회합니다.
     */
    @Query("select presentation from Presentation presentation " +
            "where presentation.endDate >= :date " +
            "and presentation.center.id = :centerId")
    Presentation findByCenterIdAndDate(@Param("centerId") Long centerId, @Param("date") LocalDate date);
}