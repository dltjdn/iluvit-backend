package FIS.iLUVit.domain.chat.dto;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.chat.domain.Chat;
import FIS.iLUVit.domain.chat.domain.ChatRoom;
import FIS.iLUVit.domain.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access= AccessLevel.PRIVATE)
public class ChatRoomFindAllResponse {
    @JsonProperty("room_id")
    private Long roomId;
    @JsonProperty("center_id")
    private Long centerId;
    private String centerName; // center_id null이면 모두의 이야기
    @JsonProperty("board_id")
    private Long boardId;
    private String boardName;
    private String recentMessage;
    private LocalDate date;
    private LocalTime time;
    private Boolean anonymous;
    @JsonProperty("opponent_id")
    private Long opponentId;
    @JsonProperty("opponent_nickname")
    private String opponentNickname;
    
    @JsonProperty("opponent_image")
    private String opponentImage;

    @JsonProperty("receiver_id")
    private Long receiverId;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("comment_id")
    private Long commentId;

    public static ChatRoomFindAllResponse from(ChatRoom chatRoom){
        ChatRoomFindAllResponseBuilder builder = ChatRoomFindAllResponse.builder()
                .roomId(chatRoom.getId())
                .recentMessage(chatRoom.getMessage())
                .date(chatRoom.getDate())
                .time(chatRoom.getTime())
                .anonymous(chatRoom.getAnonymous())
                .receiverId(chatRoom.getReceiver().getId());

        if (chatRoom.getPost() != null) {
            builder.boardId(chatRoom.getPost().getBoard().getId())
                    .boardName(chatRoom.getPost().getBoard().getName())
                    .postId(chatRoom.getPost().getId());

            if (chatRoom.getPost().getBoard().getCenter() != null) {
                builder.centerId(chatRoom.getPost().getBoard().getCenter().getId())
                    .centerName(chatRoom.getPost().getBoard().getCenter().getName() + "의 이야기");
            } else {
                builder.centerName("모두의 이야기");
            }
        }

        if (chatRoom.getSender() != null) {
            builder.opponentId(chatRoom.getSender().getId())
                    .opponentNickname(chatRoom.getSender().getNickName());

            if(chatRoom.getSender().getProfileImagePath() != null ){
                builder.opponentImage(chatRoom.getSender().getProfileImagePath());
            }
        }

        if (chatRoom.getComment() != null) {
            builder.commentId(chatRoom.getComment().getId());
        }

        return builder.build();

    }

}
