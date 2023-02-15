package FIS.iLUVit.dto.chat;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    @NotBlank(message = "메시지에 값을 채워주세요.")
    private String message;

    @NotBlank(message = "게시글 id 필요")
    private Long post_id;

    private Long comment_id; // 댓글 작성자한테 쪽지 보낸 경우 comment_id도 필요

    public void addMessage(String message){
        this.message = message;
    }

    public void addPostId(Long postId){
        this.post_id = postId;
    }

    public ChatRequest(String message, Long post_id){
        this.message= message;
        this.post_id= post_id;
    }


}
