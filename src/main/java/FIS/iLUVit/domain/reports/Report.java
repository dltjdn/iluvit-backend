package FIS.iLUVit.domain.reports;

import FIS.iLUVit.domain.BaseEntity;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportStatus;
import FIS.iLUVit.domain.enumtype.ReportReason;
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
public abstract class Report extends BaseEntity {

    @Id
    @GeneratedValue
    protected Long id;
    @Enumerated(value = EnumType.STRING)
    protected ReportReason reason;                     // 신고 사유
    @Enumerated(value = EnumType.STRING)
    protected ReportStatus status;                     // 처리 상태
    protected LocalDate date;                          // 신고 접수 날짜
    protected LocalTime time;                          // 신고 접수 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    protected User targetUser;                         // 피신고자(해당 게시글,댓글의 작성자)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    protected User user;                               // 신고자

    @Column(name = "dtype", insertable = false, updatable = false)
    protected String dtype;                            // 게시글,댓글 구분

    public Report(User user, User targetUser, ReportReason reason){
        this.user = user;
        this.targetUser = targetUser;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.reason = reason;
        this.status = ReportStatus.ACCEPT;
    }
}