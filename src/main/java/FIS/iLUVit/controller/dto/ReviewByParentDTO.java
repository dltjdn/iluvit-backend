package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ReviewByParentDTO {
    private List<ReviewDto> reviews = new ArrayList<>();

    @Data
    @AllArgsConstructor
    static public class ReviewDto {

        private Long reviewId;
        private Long centerId;
        private String centerName;
        private Integer centerScore;
        private String content;
        private LocalTime createTime;
        private LocalTime updateTime;

        public ReviewDto(Review review) {
            this.reviewId = review.getId();
            this.centerId = review.getCenter().getId();
            this.centerName = review.getCenter().getName();
            this.centerScore = review.getCenter().getScore();
            this.content = review.getContent();
            this.createTime = review.getCreateTime();
            this.updateTime = review.getUpdateTime();
        }
    }
}
