package FIS.iLUVit.dto.review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Lob;

@Getter
@NoArgsConstructor
public class ReviewCommentDto {

    @Lob
    private String comment;

}
