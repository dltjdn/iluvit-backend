package FIS.iLUVit.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CreateChatRequest {

    @NotBlank(message = "메시지에 값을 채워주세요.")
    private String message;

    @NotBlank(message = "게시글 id 필요")
    private Long post_id;

    private Long comment_id; // 댓글 작성자한테 쪽지 보낸 경우 comment_id도 필요

    @NotBlank(message = "받는 사람 id 필요")
    private Long receiver_id;
}
