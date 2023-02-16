package FIS.iLUVit.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Lob;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentDto {

    @Lob
    private String comment;

}
