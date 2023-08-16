package FIS.iLUVit.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Lob;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentRequest {

    @Lob
    private String comment;

}
