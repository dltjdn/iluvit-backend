package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.exception.PresentationException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Participation extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "ptDate_id")
    private PtDate ptDate;

    @Builder
    public Participation(Parent parent, PtDate ptDate, Status status) {
        this.parent = parent;
        this.ptDate = ptDate;
        this.status = status;
    }

    public static Participation createAndRegister(Parent parent, PtDate ptDate) {

        // participation 생성
        Participation participation = Participation.builder()
                .parent(parent)
                .ptDate(ptDate)
                .status(Status.JOINED)
                .build();

        // 연관 관계 등록
        parent.getParticipations().add(participation);

        // 연관 관계 등록 및 participationCnt + 1
        PtDate.acceptParticipation(participation);

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

    // 취소할 경우 연관관계를 해제해야한다.
    public static Participation cancel(Participation participation) {
        PtDate.cancelParticipation(participation);
        participation.status = Status.CANCELED;
        return participation;
    }
}
