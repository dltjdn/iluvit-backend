package FIS.iLUVit.domain.reports;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportReason;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
public class CommentReport extends Report {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_comment_id")
    private Comment comment;

    public CommentReport(Comment comment, User user, User targetUser, ReportReason reason){
        super(user,targetUser,reason);
        this.comment = comment;
    }
}
