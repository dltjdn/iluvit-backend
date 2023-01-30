package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewByParentDto {
    private Long reviewId;
    private Long centerId;
    private String centerName;
    private Integer score;
    private String content;

    private LocalDate createDate;
    private LocalTime createTime;
    private LocalDate updateDate;
    private LocalTime updateTime;

    public ReviewByParentDto(Review review) {
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
