package FIS.iLUVit.dto.chat;

import FIS.iLUVit.domain.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ChatDetailResponse {
    private Long roomId;
    private Long centerId;
    private String centerName; // centerId null이면 모두의 이야기
    private Long boardId;
    private String boardName;
    private Long postId;
    private Long commentId;
    private Slice<ChatInfo> chatList;
    private Long myId;
    private Boolean anonymous;
    private Long opponentId;
    private String opponentNickname;
    private String opponentImage;
    private Boolean opponentIsBlocked; // 쪽지를 보낸 유저가 차단된 유저인지 판단

    @Getter
    @NoArgsConstructor
    public static class ChatInfo {
        private Long chatId;
        private Long senderId;
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
