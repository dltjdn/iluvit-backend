package FIS.iLUVit.repository;

import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @Query("update Waiting waiting " +
            "set waiting.waitingOrder = waiting.waitingOrder - 1 " +
            "where waiting.ptDate = :ptdate")
    void updateWaitingForParticipationCancel(PtDate ptDate);

    @Query("select waiting from Waiting waiting " +
            "join fetch waiting.parent " +
            "where waiting.ptDate = :ptDate and waiting.waitingOrder = 0")
    Optional<Waiting> findMinWaitingOrder(PtDate ptDate);

}
