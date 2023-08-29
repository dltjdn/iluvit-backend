package FIS.iLUVit.dto.comment;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCreateRequest {
    private String content;
    private Boolean anonymous;
}
