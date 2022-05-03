package FIS.iLUVit.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class PtDate {
    @Id @GeneratedValue
    private Long id;

    private String dateTime;            // 설명회 날짜 시간
    private Integer ablePersonNum;      // 해당 회차에 신청 가능한 사람 수
    @OneToMany(mappedBy = "ptDate")
    private List<Waiting> waitings;       // 인원 마감이 된 회차에 대기


}
