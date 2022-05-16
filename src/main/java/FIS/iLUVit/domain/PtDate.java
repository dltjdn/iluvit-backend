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

    @ManyToOne
    @JoinColumn
    private Presentation presentation;

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

    public static PtDate createPtDate(LocalDate date, String time, Integer ablePersonNum, Integer waitingCnt, Presentation presentation) {
        return PtDate.builder()
                .date(date)
                .time(time)
                .ablePersonNum(ablePersonNum)
                .waitingCnt(waitingCnt)
                .presentation(presentation)
                .build();
    }
}
