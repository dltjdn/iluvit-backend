package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.enumtype.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    /*
        참여 현황과 설명회 참여 날짜 id로 설명회 참여 리스트를 조회합니다.
     */
    List<Participation> findByPtDateAndStatus(PtDate ptDate, Status status);

    /*
        설명회 참여 현황이 참여로 되어 있고 설명회 참여 id와 설명회 참여한 부모 id로 설명회 참여를 조회합니다.
     */
    @Query("select participation from Participation participation " +
            "join fetch participation.ptDate as ptDate " +
            "join fetch ptDate.presentation presentation " +
            "join fetch participation.parent " +
            "join fetch ptDate.participations " +
            "where participation.id = :participationId " +
            "and participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED " +
            "and participation.parent.id = :parentId")
    Optional<Participation> findByIdAndStatusWithPtDate(@Param("participationId") Long participantId, @Param("parentId") Long parentId);

    /*
        부모로 설명회 참여 리스트를 조회합니다.
     */
    List<Participation> findByParent(Parent parent);
}
