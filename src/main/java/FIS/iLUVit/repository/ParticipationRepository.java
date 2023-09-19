package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Participation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.enumtype.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    /**
     * 해당하는 설명회 회차와 상태의 설명회 참여 리스트를 조회합니다.
     */
    List<Participation> findByPtDateAndStatus(PtDate ptDate, Status status);

    /**
     * 해당하는 학부모와 상태의 설명회 참여 리스트를 조회합니다.
     */
    Slice<Participation> findByParentAndStatus(Parent parent, Status status, Pageable pageable);

    /**
     * 설명회 id와 상태, 부모에 해당하는 설명회 참여를 조회합니다
     */
    Optional<Participation> findByIdAndStatusAndParent(Long participationId,Status status, Parent parent);

    /**
      * 해당 부모의 설명회 참여 리스트를 조회합니다
      */
    List<Participation> findByParent(Parent parent);

    /**
     * 해당하는 설명회 회차와 부모, 상태의 설명회 참여를 조회합니다.
     */
    Optional<Participation> findByPtDateAndParentAndStatus(PtDate ptDate, Parent parent, Status status);

}
