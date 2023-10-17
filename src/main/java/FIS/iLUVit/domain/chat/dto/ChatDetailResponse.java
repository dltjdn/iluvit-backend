package FIS.iLUVit.domain.chat.dto;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.chat.domain.Chat;
import FIS.iLUVit.domain.chat.domain.ChatRoom;
import FIS.iLUVit.domain.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ChatDetailResponse {
    @JsonProperty("room_id")
    private Long roomId;
    @JsonProperty("center_id")
    private Long centerId;
    private String centerName; // centerId null이면 모두의 이야기

    @JsonProperty("board_id")
    private Long boardId;
    private String boardName;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("comment_id")
    private Long commentId;
    private Slice<ChatInfo> chatList;

    @JsonProperty("my_id")
    private Long myId;
    private Boolean anonymous;

    @JsonProperty("opponent_id")
    private Long opponentId;

    @JsonProperty("opponent_nickname")
    private String opponentNickname;

    @JsonProperty("opponent_image")
    private String opponentImage;
    private Boolean opponentIsBlocked; // 쪽지를 보낸 유저가 차단된 유저인지 판단

    @Getter
    @NoArgsConstructor
    public static class ChatInfo {
        @JsonProperty("chat_id")
        private Long chatId;

        @JsonProperty("sender_id")
        private Long senderId;
        @JsonProperty("receiver_id")
        private Long receiverId;
        private LocalDate date;
        private LocalTime time;
        private String message;

        public ChatInfo(Chat c) {
            this.chatId = c.getId();
            this.senderId = c.getSender().getId();
            this.receiverId = c.getReceiver().getId();
            this.date = c.getDate();
            this.time = c.getTime();
            this.message = c.getMessage();
        }
    }

    public ChatDetailResponse(ChatRoom chatRoom, Slice<ChatInfo> chatList, Boolean opponentIsBlocked) {
        Post getPost = chatRoom.getPost();
        if (getPost != null) {
            this.boardId = getPost.getBoard().getId();
            this.boardName = getPost.getBoard().getName();
            this.postId = getPost.getId();
            Center getCenter = getPost.getBoard().getCenter();
            if (getCenter != null) {
                this.centerId = getPost.getBoard().getCenter().getId();
                this.centerName = getPost.getBoard().getCenter().getName() + "의 이야기";
            } else {
                this.centerName = "모두의 이야기";
            }
        }
        this.roomId = chatRoom.getId();
        if (chatRoom.getComment() != null) {
            this.commentId = chatRoom.getComment().getId();
        }
        this.chatList = chatList;
        if (chatRoom.getReceiver() != null) {
            this.myId = chatRoom.getReceiver().getId();
        }
        this.anonymous = chatRoom.getAnonymous();
        if (chatRoom.getSender() != null) {
            this.opponentId = chatRoom.getSender().getId();
            this.opponentNickname = chatRoom.getSender().getNickName();
        }
        this.opponentIsBlocked = opponentIsBlocked;
    }

    public void updateImage(String imageUrl) {
        this.opponentImage = imageUrl;
    }

}
