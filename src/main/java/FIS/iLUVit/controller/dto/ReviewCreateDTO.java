package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateDTO {

    private Long userId;
    private String content;
    private Integer score;
    private Boolean anonymous;
}
