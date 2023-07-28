package FIS.iLUVit.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDetailRequest {
    private Long centerId;
    private String content;
    private Integer score;
    private Boolean anonymous;
}
