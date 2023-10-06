package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
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

    @Builder
    public Participation(Status status, Parent parent, PtDate ptDate) {
        this.status = status;
        this.parent = parent;
        this.ptDate = ptDate;
    }

    public static Participation createParticipation(Parent parent, Presentation presentation, PtDate ptDate, List<Participation> participations) {
        presentation.checkCanRegister(); // 설명회 기간 체크

        ptDate.checkCanRegister(); // 설명회 인원 초과 여부 체크

        checkHasRegistered(participations, parent);// 설명회 이미 등록 여부 체크

        ptDate.acceptParticipationCnt(); // 참여자 수 +1

        return Participation.builder()
                .parent(parent)
                .ptDate(ptDate)
                .status(Status.JOINED)
                .build();
    }


    // 해당 학부모가 설명회를 신청한적 있는지 확인
    public static void checkHasRegistered(List<Participation> participants, Parent parent){
        participants.forEach(participation -> {
            // 설명회를 신청한 내역과 해당 설명회가 JOINED 일 경우 Exception Handle
            if(participation.getParent().equals(parent) && participation.status == Status.JOINED)
                throw new PresentationException(PresentationErrorResult.ALREADY_PARTICIPATED);
        });
    }


    // 취소할 경우 연관관계 해제 안하고 상태만 바꿈
    public void cancelParticipation() {
        ptDate.cancelParticipation();
        status = Status.CANCELED;
    }
}
