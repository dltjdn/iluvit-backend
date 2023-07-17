package FIS.iLUVit.dto.chat;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.ChatRoom;
import FIS.iLUVit.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ChatRoomDto {
    private Long roomId;
    private Long centerId;
    private String centerName; // center_id null이면 모두의 이야기
    private Long boardId;
    private String boardName;
    private String recentMessage;
    private LocalDate date;
    private LocalTime time;
    private Boolean anonymous;
    private Long opponentId;
    private String opponentNickname;
    private String opponentImage;
    private Long receiverId;
    private Long postId;
    private Long commentId;

    public ChatRoomDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getId();
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
        this.recentMessage = chatRoom.getMessage();
        this.date = chatRoom.getDate();
        this.time = chatRoom.getTime();
        this.anonymous = chatRoom.getAnonymous();
        if (chatRoom.getSender() != null) {
            this.opponentId = chatRoom.getSender().getId();
            this.opponentNickname = chatRoom.getSender().getNickName();
        }
        this.receiverId = chatRoom.getReceiver().getId();
        if (chatRoom.getComment() != null) {
            this.commentId = chatRoom.getComment().getId();
        }
    }

    public void updateImage(String imageUrl) {
        this.opponentImage = imageUrl;
    }
}
