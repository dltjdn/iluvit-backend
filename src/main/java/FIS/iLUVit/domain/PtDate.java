package FIS.iLUVit.domain;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
public class PtDate extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    private LocalDate date;
    private String Time;            // 설명회 날짜 시간
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

}
