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

//    @Lock(LockModeType.PESSIMISTIC_WRITE)

    /*
        설명회 날짜를 파라미터로 받아서 최소 대기 주문을 조회합니다.
     */
    @Query("select waiting from Waiting waiting " +
            "join fetch waiting.parent " +
            "where waiting.ptDate = :ptDate " +
            "and waiting.waitingOrder = (select min(w.waitingOrder) from Waiting w where w.ptDate =:ptDate) ")
    Waiting findMinWaitingOrder(@Param("ptDate") PtDate ptDate);

    /*
        설명회 날짜와 변경 번호를 파라미터로 받아서 설명회 날짜 및 주문 번호로 대기들을 조회합니다.
     */
    @Query("select distinct waiting from Waiting waiting " +
            "join fetch waiting.ptDate as ptDate " +
            "join fetch ptDate.participations " +
            "where waiting.ptDate = :ptDate and waiting.waitingOrder <= :changeNum")
    List<Waiting> findWaitingsByPtDateAndOrderNum(@Param("ptDate") PtDate ptDate, @Param("changeNum") Integer changeNum);

    /*
        변경 번호와 설명회 날짜를 파라미터로 받아서 설명회 날짜 변경을 위한 대기 순서를 업데이트합니다.
     */
    @Modifying
    @Query("update Waiting waiting " +
            "set waiting.waitingOrder = waiting.waitingOrder - :changeNum " +
            "where waiting.ptDate = :ptDate")
    void updateWaitingOrderForPtDateChange(@Param("changeNum") Integer changeNum, @Param("ptDate") PtDate ptDate);

    /*
        대기 id와 사용자 id를 파라미터로 받아서 설명회 날짜에 ID로 조회합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select waiting from Waiting waiting " +
            "join fetch waiting.ptDate " +
            "where waiting.id = :waitingId and waiting.parent.id = :userId")
    Optional<Waiting> findByIdWithPtDate(@Param("waitingId") Long waitingId, @Param("userId") Long userId);

    /*
        설명회 날짜와 대기 주문을 파라미터로 받아서 대기 주문을 업데이트합니다.
     */
    @Modifying
    @Query("update Waiting waiting " +
            "set waiting.waitingOrder = waiting.waitingOrder - 1 " +
            "where waiting.waitingOrder > :waitingOrder and waiting.ptDate = :ptDate ")
    void updateWaitingOrder(@Param("ptDate")PtDate ptDate, @Param("waitingOrder") Integer waitingOrder);

    /*
        부모로 대기를 조회합니다.
     */
    List<Waiting> findByParent(Parent parent);

}
