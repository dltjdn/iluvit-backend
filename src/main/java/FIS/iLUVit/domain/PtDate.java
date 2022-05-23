package FIS.iLUVit.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class PtDate extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    private LocalDate date;
    private String time;            // 설명회 날짜 시간
    private Integer ablePersonNum;      // 해당 회차에 신청 가능한 사람 수
    private Integer participantCnt;     // 신청 사람 수
    private Integer waitingCnt;         // 대기 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Presentation presentation;

    // Set 으로 변경해야 할까?
    @OneToMany(mappedBy = "ptDate")
    private List<Participation> participations;

    @OneToMany(mappedBy = "ptDate")
    private List<Waiting> waitings;       // 인원 마감이 된 회차에 대기

    @Builder
    public PtDate(Long id, LocalDate date, String time, Integer ablePersonNum, Integer participantCnt, Integer waitingCnt, Presentation presentation) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.ablePersonNum = ablePersonNum;
        this.participantCnt = participantCnt;
        this.waitingCnt = waitingCnt;
        this.presentation = presentation;
    }

    public static PtDate register(Presentation presentation, LocalDate date, String time, Integer ablePersonNum){
        return PtDate.builder()
                .date(date)
                .time(time)
                .ablePersonNum(ablePersonNum)
                .waitingCnt(0)
                .participantCnt(0)
                .presentation(presentation)
                .build();
    }

    public static PtDate createPtDate(LocalDate date, String time, Integer ablePersonNum, Integer waitingCnt, Presentation presentation) {
        return PtDate.builder()
                .date(date)
                .time(time)
                .ablePersonNum(ablePersonNum)
                .participantCnt(0)
                .waitingCnt(waitingCnt)
                .presentation(presentation)
                .build();
    }

    // 일정을 취소할 경우 participantCnt 값을 줄인다
    public static void cancelParticipation(Participation participation) {
        participation.getPtDate().participantCnt--;
    }

    // 등록이 가능한지 여부 체크
    public boolean canRegister() {
        if(ablePersonNum <= participantCnt) return false;
        else return true;
    }

    // 설명회 신청 할경우 participantCnt 값을 1증가
    public static void acceptParticipation(Participation participation) {
        participation.getPtDate().participantCnt++;
        participation.getPtDate().participations.add(participation);
    }

    public static void cancelWaiting(Waiting waiting){
        waiting.getPtDate().waitingCnt--;
    }
}
