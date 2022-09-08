package FIS.iLUVit.domain.reports;

import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.ReportReason;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@DiscriminatorValue("POST")
@Getter
@NoArgsConstructor
public class ReportDetailPost extends ReportDetail{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_post_id")
    private Post post;

    public ReportDetailPost(Report report, User user, ReportReason reason, Post post) {
        super(report, user, reason);
        this.post = post;
    }
}
