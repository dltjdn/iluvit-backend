package FIS.iLUVit.repository;

import FIS.iLUVit.domain.PtDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface PtDateRepository extends JpaRepository<PtDate, Long> {

    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.participations as participation " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch participation.parent " +
            "where ptDate.id = :id")
    Optional<PtDate> findByIdAndJoinParticipation(@Param("id") Long ptDateId);

    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.participations as participation " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center " +
            "left join fetch participation.parent " +
            "where ptDate.id = :id and participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED")
    Optional<PtDate> findByIdAndJoinParticipationForSearch(@Param("id") Long ptDateId);

    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.waitings as waiting " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch waiting.parent " +
            "where ptDate.id = :ptDateId")
    Optional<PtDate> findByIdAndJoinWaiting(@Param("ptDateId") Long ptDateId);

    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.waitings as waiting " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center " +
            "left join fetch waiting.parent " +
            "where ptDate.id = :ptDateId")
    Optional<PtDate> findByIdAndJoinWaitingForSearch(@Param("ptDateId") Long ptDateId);

    @Modifying
    @Query("delete from PtDate ptdate where ptdate.id in :ptDateKeys")
    void deletePtDateByIds(@Param("ptDateKeys") Set<Long> ptDateKeysDeleteTarget);
}
