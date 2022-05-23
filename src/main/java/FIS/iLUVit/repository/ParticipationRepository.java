package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("select participation from Participation participation " +
            "join fetch participation.parent " +
            "join fetch participation.ptDate " +
            "where participation.id = :participationId and " +
            "participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED")
    Optional<Participation> findByIdAndJoinPresentation(@Param("participationId") Long participationId);
}
