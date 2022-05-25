package FIS.iLUVit.controller.dto;

import lombok.Data;

@Data
public class RegisterCommentRequest {
    private String content;
    private Boolean anonymous;
}
