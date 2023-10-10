package FIS.iLUVit.domain.presentation.repository;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.presentation.domain.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation, Long>, PresentationRepositoryCustom {

    /**
     * 해당 시설의 현재 진행중인 설명회를 조회합니다.
     */
    @Query("SELECT presentation FROM Presentation presentation " +
            "WHERE presentation.center.id = :centerId " +
            "AND presentation.startDate <= :date " +
            "AND presentation.endDate >= :date")
    List<Presentation> findByCenterAndDate(@Param("centerId") Long centerId, @Param("date") LocalDate date);

    /**
     * 해당 시설에 대한 설명회를 조회합니다.
     */
    List<Presentation> findByCenter(Center center);

    /**
        해당 시설에 대한 설명회 중 설명회 종료일이 현재 날짜 이후인 것을 조회합니다.
     */
    List<Presentation> findByCenterAndEndDateAfter(Center center, LocalDate date);

}