package FIS.iLUVit.domain;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.alarms.ConvertedToParticipateAlarm;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.service.AlarmUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Slf4j
@Getter
public class Waiting extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Integer waitingOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ptDate_id")
    private PtDate ptDate;

    @Builder
    public Waiting(Long id, Integer waitingOrder, Parent parent, PtDate ptDate) {
        this.id = id;
        this.waitingOrder = waitingOrder;
        this.parent = parent;
        this.ptDate = ptDate;
    }

    public static Participation whenParticipationCanceled(Waiting waiting, Presentation presentation) {
        Parent parent = waiting.parent;
        // 영속성 관리 되어서 ptDate 가져올때 쿼리 안나감
        PtDate ptDate = waiting.ptDate;
        ptDate.cancelWaitingForAcceptingParticipation();
        List<Participation> participations = ptDate.getParticipations();

        return Participation.createAndRegisterForWaitings(parent, presentation, ptDate, participations);
    }

}
