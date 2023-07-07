package FIS.iLUVit.domain.reports;

import FIS.iLUVit.domain.BaseEntity;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportStatus;
import FIS.iLUVit.domain.enumtype.ReportType;
import FIS.iLUVit.service.ReportService;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Report extends BaseEntity {

    @Id @GeneratedValue
    private Long id;
    @Column(name = "target_id")
    private Long targetId;                           // 신고 대상의 id
    @Enumerated(EnumType.STRING)
    private ReportType type;                         // 신고 대상 구분
    private int count;                               // 신고 횟수
    private LocalDate date;                          // 신고 접수 날짜
    private LocalTime time;                          // 신고 접수 시간

    @Enumerated(value = EnumType.STRING)
    private ReportStatus status;                     // 처리 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;                         // 피신고자(해당 게시글,댓글의 작성자)

    @Builder
    public Report(Long id, Long targetId, ReportType type, int count, LocalDate date, LocalTime time, ReportStatus status, User targetUser){
        this.id = id;
        this.targetId = targetId;
        this.type = type;
        this.count = count;
        this.date = date;
        this.time = time;
        this.status = status;
        this.targetUser = targetUser;
    }

    public Report(ReportType type, Long targetId,  User targetUser){
        this.targetId = targetId;
        this.type = type;
        this.count = 0;
        this.targetUser = targetUser;
        this.status = ReportStatus.ACCEPT;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    public void plusCount() {
        this.count +=1;
    }

    public void updateStatus() {
        this.status = ReportStatus.ACCEPT;
    }
}