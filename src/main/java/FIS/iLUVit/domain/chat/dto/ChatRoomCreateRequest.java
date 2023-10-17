package FIS.iLUVit.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequest {
    @NotBlank(message = "메시지에 값을 채워주세요.")
    private String message;

    @NotBlank(message = "게시글 id 필요")
    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("comment_id")
    private Long commentId; // 댓글 작성자한테 쪽지 보낸 경우 comment_id도 필요

}
