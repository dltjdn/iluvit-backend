package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.dto.presentation.PresentationForTeacherDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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