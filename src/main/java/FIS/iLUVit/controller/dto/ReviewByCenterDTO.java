package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewByCenterDTO {
    private List<ReviewCenterDto> reviews = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewCenterDto {
        private Long id;
        private String username;
        private String content;
        private Integer score;

        private LocalDate createDate;
        private LocalTime createTime;
        private LocalDate updateDate;
        private LocalTime updateTime;

        private String answer;
        private LocalDate answerCreateDate;
        private LocalTime answerCreateTime;

        private Boolean anonymous;

        private Integer like; // 좋아요 수
    }
}
