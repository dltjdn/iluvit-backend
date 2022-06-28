package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.PtDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("select distinct participation from Participation participation " +
            "join fetch participation.parent " +
            "join fetch participation.ptDate as ptDate " +
            "join fetch ptDate.participations " +
            "where participation.id = :participationId and " +
            "participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED")
    Optional<Participation> findByIdAndJoinPresentation(@Param("participationId") Long participationId);

    @Query("select participation from Participation participation " +
            "where participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED " +
            "and participation.ptDate = :ptDate")
    List<Participation> findByptDateAndStatus(@Param("ptDate") PtDate ptDate);
}
