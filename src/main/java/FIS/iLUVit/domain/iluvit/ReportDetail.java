package FIS.iLUVit.domain.iluvit;

import FIS.iLUVit.domain.iluvit.enumtype.ReportReason;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@NoArgsConstructor
public abstract class ReportDetail extends BaseEntity {

    @Id @GeneratedValue
    protected Long id;
    protected LocalDate date;                          // 신고 접수 날짜
    protected LocalTime time;                          // 신고 접수 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    protected Report report;                           // 신고 내역

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    protected User user;                               // 신고자

    @Enumerated(value = EnumType.STRING)
    protected ReportReason reason;                     // 신고 사유

    @Column(name = "dtype", insertable = false, updatable = false)
    protected String dtype;

    public ReportDetail(Report report, User user, ReportReason reason) {
        this.report = report;
        this.user = user;
        this.reason = reason;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        report.updateReportDetail(this);
    }
}
