package FIS.iLUVit.repository.iluvit;

import FIS.iLUVit.domain.iluvit.Parent;
import FIS.iLUVit.domain.iluvit.PtDate;
import FIS.iLUVit.domain.iluvit.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select waiting from Waiting waiting " +
            "join fetch waiting.parent " +
            "where waiting.ptDate = :ptDate " +
            "and waiting.waitingOrder = (select min(w.waitingOrder) from Waiting w where w.ptDate =:ptDate) ")
    Waiting findMinWaitingOrder(@Param("ptDate") PtDate ptDate);

    @Query("select distinct waiting from Waiting waiting " +
            "join fetch waiting.ptDate as ptDate " +
            "join fetch ptDate.participations " +
            "where waiting.ptDate = :ptDate and waiting.waitingOrder <= :changeNum")
    List<Waiting> findWaitingsByPtDateAndOrderNum(@Param("ptDate") PtDate ptDate, @Param("changeNum") Integer changeNum);

    @Modifying
    @Query("update Waiting waiting " +
            "set waiting.waitingOrder = waiting.waitingOrder - :changeNum " +
            "where waiting.ptDate = :ptDate")
    void updateWaitingOrderForPtDateChange(@Param("changeNum") Integer changeNum, @Param("ptDate") PtDate ptDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select waiting from Waiting waiting " +
            "join fetch waiting.ptDate " +
            "where waiting.id = :waitingId and waiting.parent.id = :userId")
    Optional<Waiting> findByIdWithPtDate(@Param("waitingId") Long waitingId, @Param("userId") Long userId);

    @Modifying
    @Query("update Waiting waiting " +
            "set waiting.waitingOrder = waiting.waitingOrder - 1 " +
            "where waiting.waitingOrder > :waitingOrder and waiting.ptDate = :ptDate ")
    void updateWaitingOrder(@Param("ptDate")PtDate ptDate, @Param("waitingOrder") Integer waitingOrder);

    List<Waiting> findByParent(Parent parent);

}
