package FIS.iLUVit.domain.iluvit.reports;

import FIS.iLUVit.domain.iluvit.Comment;
import FIS.iLUVit.domain.iluvit.User;
import FIS.iLUVit.domain.iluvit.enumtype.ReportReason;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("COMMENT")
@Getter
@NoArgsConstructor
public class ReportDetailComment extends ReportDetail {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_comment_id")
    private Comment comment;

    @Builder
    public ReportDetailComment(Long id, LocalDate date, LocalTime time, Report report, User user, ReportReason reason, String dtype, Comment comment){
        this.id = id;
        this.date = date;
        this.time = time;
        this.report = report;
        this.user = user;
        this.reason = reason;
        this.dtype = dtype;
        this.comment = comment;
    }

    public ReportDetailComment(Report report, User user, ReportReason reason, Comment comment) {
        super(report, user, reason);
        this.comment = comment;
    }
}
