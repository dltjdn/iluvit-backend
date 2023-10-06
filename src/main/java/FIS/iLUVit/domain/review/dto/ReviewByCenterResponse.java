package FIS.iLUVit.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewByCenterResponse {
    private Long id;
    private Long parentId;
    private String username;
    private String content;
    private Integer score;

    private LocalDate createDate;
    private LocalTime createTime;
    private LocalDate updateDate;
    private LocalTime updateTime;

    private Long teacherId;
    private String answer;
    private LocalDate answerCreateDate;
    private LocalTime answerCreateTime;

    private Boolean anonymous;

    private Integer like; // 좋아요 수

    private String profileImage;

}
