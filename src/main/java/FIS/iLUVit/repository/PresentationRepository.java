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
        설명회 시설 id가 시설 id와 같고 설명회 날짜가 설명회 끝나는 날보다 작거나 같은 설명회 객체를 필터링하여 설명회 DTO 리스트를 불러옵니다.
     */
    @Query("select new FIS.iLUVit.dto.presentation.PresentationWithPtDatesDto" +
            "(p.id, p.startDate, p.endDate, p.place, p.content, p.imgCnt, p.videoCnt, p.infoImagePath, pd.id, pd.date, pd.time, pd.ablePersonNum, pd.participantCnt, pd.waitingCnt) " +
            "from Presentation p " +
            "join p.ptDates as pd " +
            "where p.center.id = :centerId " +
            "and :date <= p.endDate")
    List<PresentationWithPtDatesDto> findByCenterAndDateWithPtDates(@Param("centerId") Long centerId, @Param("date") LocalDate date);

    /*
        설명회 시설 id가 시설 id와 같고, 설명회 시작날짜가 설명회 날짜보다 작거나 같고, 설명회 날짜가 설명회 끝날짜보다 작거나 같은 설명회 객체를 필터링하여 설명회 DTO 리스트를 불러옵니다.
     */
    @Query("select new FIS.iLUVit.dto.presentation.PresentationWithPtDatesDto" +
            "(p.id, p.startDate, p.endDate, p.place, p.content, p.imgCnt, p.videoCnt, p.infoImagePath, pd.id, pd.date, pd.time, pd.ablePersonNum, pd.participantCnt, pd.waitingCnt, participation.id ,waiting.id) " +
            "from Presentation p " +
            "join p.ptDates as pd " +
            "left join pd.participations as participation on participation.parent.id = :userId and participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED " +
            "left join pd.waitings as waiting on waiting.parent.id = :userId " +
            "where p.center.id = :centerId " +
            "and p.startDate <= :date " +
            "and :date <= p.endDate")
    List<PresentationWithPtDatesDto> findByCenterAndDateWithPtDates(@Param("centerId") Long centerId, @Param("date") LocalDate date, @Param("userId") Long userId);

    /*
        설명회 시설 id로 선생님을 위한 설명회 DTO 리스트를 불러옵니다.
     */
    @Query("select new FIS.iLUVit.dto.presentation.PresentationForTeacherDto(p.id, p.startDate, p.endDate, p.place, p.content, p.infoImagePath) " +
            "from Presentation p " +
            "where p.center.id = :centerId")
    List<PresentationForTeacherDto> findByCenterId(@Param("centerId") Long centerId, Pageable pageable);

    /*
        설명회 id와 설명회 가입날짜로 설명회를 불러옵니다.
     */
    @Query("select distinct presentation from Presentation presentation " +
            "join fetch presentation.ptDates " +
            "join fetch presentation.center " +
            "where presentation.id = :presentationId")
    Optional<Presentation> findByIdAndJoinPtDate(@Param("presentationId") Long presentationId);

    /*
        설명회 종료일이 설명회 날짜보다 크거나 같고, 설명회 시설 id가 시설 id와 같은 설명회를 불러옵니다.
     */
    @Query("select presentation from Presentation presentation " +
            "where presentation.endDate >= :date " +
            "and presentation.center.id = :centerId")
    Presentation findByCenterIdAndDate(@Param("centerId") Long centerId, @Param("date") LocalDate date);
}