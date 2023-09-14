package FIS.iLUVit.domain;

import FIS.iLUVit.dto.presentation.PtDateDto;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
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
    private Integer participantCnt;     // 신청 사람 수

    @Version
    private Integer version;

    private Integer waitingCnt;         // 대기 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Presentation presentation;

    // Set 으로 변경해야 할까?
    @OneToMany(mappedBy = "ptDate")
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "ptDate")
    private List<Waiting> waitings = new ArrayList<>();       // 인원 마감이 된 회차에 대기

    @Builder(toBuilder = true)
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
        //presentation.getPtDates().add(ptDate);
    }

    // 일정을 취소할 경우 participantCnt 값을 줄인다
    public void cancelParticipation() {
        participantCnt--;
    }

    public PtDate acceptWaiting(Waiting waiting) {
        waitings.add(waiting);
        return this;
    }

    // 등록이 가능한지 여부 체크
    public boolean canRegister() {
        if (ablePersonNum <= participantCnt) return false;
        else return true;
    }

    // 설명회 신청 할경우 participantCnt 값을 1증가
    public void acceptParticipation(Participation participation) {
        participantCnt++;
        participations.add(participation);
    }

    public void cancelWaitingForAcceptingParticipation() {
        // 쿼리문 나가지 않기위한 waitings 초기화 안하기
        waitingCnt--;
    }

    public boolean hasWaiting(){
        if(waitingCnt > 0)
            return true;
        else return false;
    }

    public PtDate update(PtDateDto ptDateDto) {
        if(participantCnt > ptDateDto.getAblePersonNum())
            throw new PresentationException(PresentationErrorResult.UPDATE_ERROR_ABLE_PERSON_NUM);
        date = ptDateDto.getDate();
        time = ptDateDto.getTime();
        ablePersonNum = ptDateDto.getAblePersonNum();
        return this;
    }

    public void canDelete() {
        if(participantCnt > 0)
            throw new PresentationException(PresentationErrorResult.DELETE_FAIL_HAS_PARTICIPANT);
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

    public PtDate updateWaitingCntForPtDateChange(Integer changeNum) {
//        participantCnt += changeNum;
        waitingCnt =- changeNum;
        if(waitingCnt < 0)
            waitingCnt = 0;
        return this;
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
