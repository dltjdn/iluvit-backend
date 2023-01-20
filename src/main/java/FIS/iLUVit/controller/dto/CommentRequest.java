package FIS.iLUVit.controller.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Boolean anonymous;
}
