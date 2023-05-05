package FIS.iLUVit.domain.iluvit;

import FIS.iLUVit.domain.iluvit.enumtype.ReportReason;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("POST")
@Getter
@NoArgsConstructor
public class ReportDetailPost extends ReportDetail{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_post_id")
    private Post post;

    @Builder
    public ReportDetailPost(Long id, LocalDate date, LocalTime time, Report report, User user, ReportReason reason, String dtype, Post post){
        this.id = id;
        this.date = date;
        this.time = time;
        this.report = report;
        this.user = user;
        this.reason = reason;
        this.dtype = dtype;
        this.post = post;
    }

    public ReportDetailPost(Report report, User user, ReportReason reason, Post post) {
        super(report, user, reason);
        this.post = post;
    }
}
