package FIS.iLUVit.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegisterCommentRequest {
    private String content;
    private Boolean anonymous;
}
