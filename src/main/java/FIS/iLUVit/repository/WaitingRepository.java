package FIS.iLUVit.repository;

import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @Modifying
    @Query("update Waiting waiting " +
            "set waiting.waitingOrder = waiting.waitingOrder - 1 " +
            "where waiting.ptDate = :ptDate")
    void updateWaitingForParticipationCancel(@Param("ptDate") PtDate ptDate);

    @Query("select waiting from Waiting waiting " +
            "join fetch waiting.parent " +
            "where waiting.ptDate = :ptDate and waiting.waitingOrder = 0")
    Optional<Waiting> findMinWaitingOrder(@Param("ptDate") PtDate ptDate);

}
