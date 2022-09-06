package FIS.iLUVit.domain.reports;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportReason;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@DiscriminatorValue("COMMENT")
@Getter
@NoArgsConstructor
public class ReportDetailComment extends ReportDetail {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_comment_id")
    private Comment comment;

    public ReportDetailComment(Report report, User user, ReportReason reason, Comment comment) {
        super(report, user, reason);
        this.comment = comment;
    }
}
