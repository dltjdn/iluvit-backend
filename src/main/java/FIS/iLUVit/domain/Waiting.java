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

    public static void hasRegistered(List<Waiting> waitings, Parent parent) {
        waitings.forEach(waiting -> {
            if (waiting.parent.equals(parent))
                throw new PresentationException("이미 대기등록을 마친 학부모 입니다");
        });
    }

    public static Waiting createAndRegister(Parent parent, Presentation presentation, PtDate ptDate, List<Participation> participations) {
        // 기간안에 신청한 것이 맞는가?
        presentation.canRegister();
        // ptDate 에 초과가 된게 맞는가?
        ptDate.canWait();
        // 1. 유효성 검사 waiting 에 존재?
        hasRegistered(ptDate.getWaitings(), parent);
        // 2. 학부모 신청자에 포함 되어 있나?
        Participation.hasRegistered(participations, parent);
        Waiting waiting = Waiting
                .builder()
                .waitingOrder(ptDate.getWaitingCnt() + 1)
                .parent(parent)
                .ptDate(ptDate)
                .build();
        ptDate.acceptWaiting(waiting);
        return waiting;
    }



}
