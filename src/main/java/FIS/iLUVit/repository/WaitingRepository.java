package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    /*
        대기중인 ptDate가 ptDate와 같고 가장 작은 waitingOrder 값을 가지는 Waiting을 조회합니다.
     */
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select waiting from Waiting waiting " +
            "join fetch waiting.parent " +
            "where waiting.ptDate = :ptDate " +
            "and waiting.waitingOrder = (select min(w.waitingOrder) from Waiting w where w.ptDate =:ptDate) ")
    Waiting findMinWaitingOrder(@Param("ptDate") PtDate ptDate);

    /*
        대기중인 ptDate가 ptDate와 같고 대기중인 waitingOrder가 변경 숫자보다 작거나 같으면 Waiting 리스트를 불러옵니다.
     */
    @Query("select distinct waiting from Waiting waiting " +
            "join fetch waiting.ptDate as ptDate " +
            "join fetch ptDate.participations " +
            "where waiting.ptDate = :ptDate and waiting.waitingOrder <= :changeNum")
    List<Waiting> findWaitingsByPtDateAndOrderNum(@Param("ptDate") PtDate ptDate, @Param("changeNum") Integer changeNum);

    /*
        대기중인 ptDate가 ptdate와 같으면 대기중인 waitingOrder의 값을 바뀐 숫자만큼 빼서 업데이트시킵니다. (은행 번호표 뽑고 기다리면 점점 내 차례오는거라고 생각하면됨)
     */
    @Modifying
    @Query("update Waiting waiting " +
            "set waiting.waitingOrder = waiting.waitingOrder - :changeNum " +
            "where waiting.ptDate = :ptDate")
    void updateWaitingOrderForPtDateChange(@Param("changeNum") Integer changeNum, @Param("ptDate") PtDate ptDate);

    /*
        waitingId와 userId 매개변수에 해당하는 조건을 만족하는 Waiting 엔티티를 조회합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select waiting from Waiting waiting " +
            "join fetch waiting.ptDate " +
            "where waiting.id = :waitingId and waiting.parent.id = :userId")
    Optional<Waiting> findByIdWithPtDate(@Param("waitingId") Long waitingId, @Param("userId") Long userId);

    /*
        대기중인 waitingOrder가 waitingOrder 값보다 크고 대기중인 ptDate와 일치한다면 waitingOrder를 1 빼서 업데이트시킵니다.
     */
    @Modifying
    @Query("update Waiting waiting " +
            "set waiting.waitingOrder = waiting.waitingOrder - 1 " +
            "where waiting.waitingOrder > :waitingOrder and waiting.ptDate = :ptDate ")
    void updateWaitingOrder(@Param("ptDate")PtDate ptDate, @Param("waitingOrder") Integer waitingOrder);

    /*
        부모로 Waiting 리스트를 조회합니다.
     */
    List<Waiting> findByParent(Parent parent);

    List<Waiting> findByPtDate(PtDate ptDate);

}
