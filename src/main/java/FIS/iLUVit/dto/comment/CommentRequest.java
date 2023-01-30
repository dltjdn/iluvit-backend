package FIS.iLUVit.dto.comment;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Boolean anonymous;
}
