package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.ChatRoom;
import FIS.iLUVit.domain.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ChatListDto {

    private Long room_id;

    private Long center_id;
    private String centerName; // center_id null이면 모두의 이야기

    private Long board_id;
    private String boardName;

    private String recentMessage;
    private LocalDate date;
    private LocalTime time;

    private Boolean anonymous;
    private Long opponent_id;
    private String opponent_nickname;
    private String opponent_image;

    private Long receiver_id;

    private Long post_id;
    private Long comment_id;

    public ChatListDto(ChatRoom chatRoom) {
        this.room_id = chatRoom.getId();
        Post getPost = chatRoom.getPost();
        if (getPost != null) {
            this.board_id = getPost.getBoard().getId();
            this.boardName = getPost.getBoard().getName();
            this.post_id = getPost.getId();
            Center getCenter = getPost.getBoard().getCenter();
            if (getCenter != null) {
                this.center_id = getPost.getBoard().getCenter().getId();
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
            this.opponent_id = chatRoom.getSender().getId();
            this.opponent_nickname = chatRoom.getSender().getNickName();
        }
        this.receiver_id = chatRoom.getReceiver().getId();
        if (chatRoom.getComment() != null) {
            this.comment_id = chatRoom.getComment().getId();
        }
    }

    public void updateImage(String imageUrl) {
        this.opponent_image = imageUrl;
    }
}
