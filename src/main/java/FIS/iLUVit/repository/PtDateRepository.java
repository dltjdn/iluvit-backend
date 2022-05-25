package FIS.iLUVit.repository;

import FIS.iLUVit.domain.PtDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PtDateRepository extends JpaRepository<PtDate, Long> {

    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.participations as participation " +
            "left join fetch participation.parent " +
            "where ptDate.id = :id")
    Optional<PtDate> findByIdAndJoinParticipation(@Param("id") Long ptDateId);

    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.waitings as waiting " +
            "left join fetch waiting.parent " +
            "where ptDate.id = :ptDateId")
    Optional<PtDate> findByIdAndJoinWaiting(@Param("ptDateId") Long ptDateId);

}
