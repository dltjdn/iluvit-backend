package FIS.iLUVit.domain.comment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCreateRequest {
    private String content;
    private Boolean anonymous;
}
