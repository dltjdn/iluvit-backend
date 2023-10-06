package FIS.iLUVit.domain.waiting.repository;

import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.ptdate.domain.PtDate;
import FIS.iLUVit.domain.waiting.domain.Waiting;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    /**
     * 해당 학부모로 Waiting 리스트를 조회합니다
     */
    List<Waiting> findByParent(Parent parent);

    Slice<Waiting> findByParent(Parent parent, Pageable pageable);

    /**
     * 해당 설명회 회차로 Waiting 리스트를 조회합니다
     */
    List<Waiting> findByPtDate(PtDate ptDate);

    /**
     * 주어진 waitingId를 가지며 해당 학부모가 대기 신청한 Waiting 엔티티를 조회합니다
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Waiting> findByIdAndParent(Long waitingId, Parent parent);

    /**
     * 해당 설명회 회차에서 대기순번이 가장 낮은 Waiting 엔티티를 조회합니다
     */
    Optional<Waiting> findFirstByPtDateOrderByWaitingOrderAsc(PtDate ptDate);

    /**
     * 해당 설명회 회차의 대기순번이 changeNum보다 같거나 작은 Waiting 리스트를 조회합니다
     */
    List<Waiting> findByPtDateAndWaitingOrderLessThanEqual(PtDate ptDate, Integer changeNum);

    /**
     * 해당 설명회 회차와 신청한 학부모로 대기 리스트를 조회합니다
     */
    Optional<Waiting> findByPtDateAndParent(PtDate ptDate, Parent parent);

    /**
     * Waiting Id 리스트를 받아 해당 Waiting들을 삭제합니다.
     */
    void deleteByIdIn(List<Long> waitingIds);

    /**
     * 주어진 ptDate에 해당하는 Waiting의 waitingOrder 값을 -changeNum 만큼 감소시킵니다
     */
    @Modifying
    @Query("UPDATE Waiting waiting " +
            "SET waiting.waitingOrder = waiting.waitingOrder - :changeNum " +
            "WHERE waiting.ptDate = :ptDate")
    void updateWaitingOrderForPtDateChange(@Param("changeNum") Integer changeNum, @Param("ptDate") PtDate ptDate);

    /**
     * 주어진 ptDate에 해당하는 Waiting 중에서 waitingOrder 값이 주어진 waitingOrder보다 큰 엔티티들의 waitingOrder 값을 1씩 감소시킵니다
     */
    @Modifying
    @Query("UPDATE Waiting waiting " +
            "SET waiting.waitingOrder = waiting.waitingOrder - 1 " +
            "WHERE waiting.waitingOrder > :waitingOrder AND waiting.ptDate = :ptDate ")
    void updateWaitingOrder(@Param("ptDate")PtDate ptDate, @Param("waitingOrder") Integer waitingOrder);

}
