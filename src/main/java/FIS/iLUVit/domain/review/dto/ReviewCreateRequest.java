package FIS.iLUVit.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequest {
    private Long centerId;
    private String content;
    private Integer score;
    private Boolean anonymous;
}
