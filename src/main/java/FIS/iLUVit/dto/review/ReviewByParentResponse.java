package FIS.iLUVit.dto.review;

import FIS.iLUVit.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewByParentResponse {
    private Long reviewId;
    private Long centerId;
    private String centerName;
    private Integer score;
    private String content;
    private LocalDate createDate;
    private LocalTime createTime;
    private LocalDate updateDate;
    private LocalTime updateTime;

    public ReviewByParentResponse(Review review) {
        this.reviewId = review.getId();
        this.centerId = review.getCenter().getId();
        this.centerName = review.getCenter().getName();
        this.score = review.getScore();
        this.content = review.getContent();
        this.createDate = review.getCreateDate();
        this.createTime = review.getCreateTime();
        this.updateDate = review.getUpdateDate();
        this.updateTime = review.getUpdateTime();
    }

}
