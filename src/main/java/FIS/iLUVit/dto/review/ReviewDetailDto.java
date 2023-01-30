package FIS.iLUVit.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDetailDto {

    private Long centerId;
    private String content;
    private Integer score;
    private Boolean anonymous;
}
