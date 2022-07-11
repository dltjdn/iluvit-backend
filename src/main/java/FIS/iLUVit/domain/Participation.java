package FIS.iLUVit.domain;

import FIS.iLUVit.domain.alarms.PresentationConvertedToParticipateAlarm;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.service.AlarmUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class Participation extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ptDate_id")
    private PtDate ptDate;

    public Participation(Parent parent, PtDate ptDate, Status status) {
        this.parent = parent;
        this.ptDate = ptDate;
        this.status = status;
    }

    public static Participation createAndRegister(Parent parent, Presentation presentation, PtDate ptDate, List<Participation> participations) {
        presentation.canRegister();
        // 설명회 인원 초과가 되었으면 신청 불가
        if (!ptDate.canRegister()) {
            throw new PresentationException("설명회 수용가능 인원이 초과 되었습니다 대기자로 등록해 주세요");
        }
        // 설명회에 등록되어 있으면 신청 불가
        Participation.hasRegistered(participations, parent);

        // participation 생성
        Participation participation = Participation.builder()
                .parent(parent)
                .ptDate(ptDate)
                .status(Status.JOINED)
                .build();

        // 연관 관계 등록
        parent.getParticipations().add(participation);

        // 연관 관계 등록 및 participationCnt + 1
        ptDate.acceptParticipation(participation);

        return participation;
    }

    // 해당 학부모가 설명회를 신청한적 있는지 확인
    public static void hasRegistered(List<Participation> participants, Parent parent){
        participants.forEach(participation -> {
            // 설명회를 신청한 내역과 해당 설명회가 JOINED 일 경우 Exception Handle
            if(participation.getParent().equals(parent) && participation.status == Status.JOINED)
                throw new PresentationException("이미 설명회를 신청하셨습니다.");
        });
    }

    // 대기 상태에서 신청으로 전환시 발생하는 createAndRegister 메서드
    public static Participation createAndRegisterForWaitings(Parent parent, Presentation presentation, PtDate ptDate, List<Participation> participations) {
        Participation.hasRegistered(participations, parent);
        // participation 생성
        Participation participation = Participation.builder()
                .parent(parent)
                .ptDate(ptDate)
                .status(Status.JOINED)
                .build();

        // 연관 관계 등록
        parent.getParticipations().add(participation);

        // 연관 관계 등록 및 participationCnt + 1
        ptDate.acceptParticipation(participation);
        AlarmUtils.publishAlarmEvent(new PresentationConvertedToParticipateAlarm(parent, presentation, presentation.getCenter()));
        return participation;
    }

    // 취소할 경우 연관관계 해제 안하고 상태만 바꿈
    public Participation cancel() {
        ptDate.cancelParticipation();
        status = Status.CANCELED;
        return this;
    }
}
