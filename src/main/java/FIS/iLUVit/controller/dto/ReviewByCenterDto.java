package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewByCenterDto {
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
