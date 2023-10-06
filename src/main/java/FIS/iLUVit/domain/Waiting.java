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
    public Waiting(Integer waitingOrder, Parent parent, PtDate ptDate) {
        this.waitingOrder = waitingOrder;
        this.parent = parent;
        this.ptDate = ptDate;
    }

    public static Waiting createWaiting(Integer waitingOrder, Parent parent, PtDate ptDate){
        return Waiting.builder()
                .waitingOrder(waitingOrder)
                .parent(parent)
                .ptDate(ptDate)
                .build();
    }

}
