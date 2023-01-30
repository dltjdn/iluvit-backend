package FIS.iLUVit.dto.review;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;

@Data
@NoArgsConstructor
public class ReviewCommentDto {

    @Lob
    private String comment;

}
