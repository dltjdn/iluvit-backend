package FIS.iLUVit.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Getter
public class Waiting extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    private Integer waitingOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ptDate_id")
    private PtDate ptDate;

    public static Participation whenParticipationCanceled(Waiting waiting) {
        Parent parent = waiting.parent;
        log.info("쿼리가 나갈까??");
        PtDate ptDate = waiting.ptDate;
        log.info("쿼리가 나갈까??");
        PtDate.cancelWaiting(waiting);
        Participation.hasRegistered(ptDate.getParticipations(), parent);
        return Participation.createAndRegister(parent, ptDate);
    }
}
