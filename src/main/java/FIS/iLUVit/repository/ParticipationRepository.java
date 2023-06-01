package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    /*
        설명회 날짜 id를 파라미터로 받아서 설명회 날짜 및 가입 상태로 조회합니다.
     */
    @Query("select participation from Participation participation " +
            "where participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED " +
            "and participation.ptDate.id = :ptDateId")
    List<Participation> findByPtDateAndStatusJOINED(@Param("ptDateId") Long ptDateId);

    /*
        설명회 id와 부모 id를 파라미터로 받아서 설명회 날짜 ID 및 상태로 조회합니다.
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
        부모로 조회합니다.
     */
    List<Participation> findByParent(Parent parent);
}
