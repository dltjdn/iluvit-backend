package FIS.iLUVit.domain.ptdate.domain;

import FIS.iLUVit.domain.participation.domain.Participation;
import FIS.iLUVit.domain.presentation.domain.Presentation;
import FIS.iLUVit.domain.ptdate.dto.PtDateDto;
import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.presentation.exception.PresentationErrorResult;
import FIS.iLUVit.domain.presentation.exception.PresentationException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
public class PtDate extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;
    private String time;            // 설명회 날짜 시간
    private Integer ablePersonNum;      // 해당 회차에 신청 가능한 사람 수
    private Integer participantCnt;     // 신청자 수

    @Version
    private Integer version;

    private Integer waitingCnt;         // 대기자  수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Presentation presentation;

    @OneToMany(mappedBy = "ptDate")
    private List<Participation> participations = new ArrayList<>();


    @Builder
    public PtDate(LocalDate date, String time, Integer ablePersonNum, Integer participantCnt, Integer waitingCnt, Presentation presentation) {
        this.date = date;
        this.time = time;
        this.ablePersonNum = ablePersonNum;
        this.participantCnt = participantCnt;
        this.waitingCnt = waitingCnt;
        this.presentation = presentation;
    }

    public static PtDate createPtDate(Presentation presentation, PtDateDto ptDateCreateDto) {
        return PtDate.builder()
                .date(ptDateCreateDto.getDate())
                .time(ptDateCreateDto.getTime())
                .ablePersonNum(ptDateCreateDto.getAblePersonNum())
                .waitingCnt(0)
                .participantCnt(0)
                .presentation(presentation)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PtDate ptDate = (PtDate) o;
        if (id == null) return false;
        return id.equals(ptDate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void updatePtDate(PtDateDto ptDateDto) {
        if(participantCnt > ptDateDto.getAblePersonNum())
            throw new PresentationException(PresentationErrorResult.INSUFFICIENT_CAPACITY_SETTING);

        date = ptDateDto.getDate();
        time = ptDateDto.getTime();
        ablePersonNum = ptDateDto.getAblePersonNum();
    }


    // 일정을 취소할 경우 participantCnt 값을 줄인다
    public void cancelParticipation() {
        participantCnt--;
    }

    // 등록이 가능한지 여부 체크
    public void checkCanRegister() {
        if (ablePersonNum <= participantCnt)
            throw new PresentationException(PresentationErrorResult.CAPACITY_EXCEEDED);

    }

    public void checkCanNotRegister(){
        if(ablePersonNum > participantCnt)
            throw new PresentationException(PresentationErrorResult.NOT_REACHED_CAPACITY_FOR_WAIT);
    }

    // 설명회 신청 할경우 participantCnt 값을 1증가
    public void acceptParticipationCnt() {
        participantCnt++;
    }

    public void cancelWaitingForAcceptingParticipation() {
        waitingCnt--;
    }

    public boolean checkHasWaiting(){
        if(waitingCnt > 0)
            return true;
        else return false;
    }

    public boolean checkCanDelete() {
        if(participantCnt > 0)
            throw new PresentationException(PresentationErrorResult.CANNOT_DELETE_WITH_PARTICIPANT);
        return true;
    }


    public void updateWaitingCntForPtDateChange(Integer changeNum) {
        waitingCnt =- changeNum;
        if(waitingCnt < 0) waitingCnt = 0;
    }

    public void resetWaitingCnt() {
        waitingCnt = 0;
    }

    public PtDate increaseWaitingCnt() {
        waitingCnt++;
        return this;
    }

    public PtDate decreaseWaitingCnt() {
        waitingCnt--;
        return this;
    }
}
