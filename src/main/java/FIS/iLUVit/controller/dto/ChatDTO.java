package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ChatDTO {

    private Long room_id;

    private Long center_id;
    private String centerName; // center_id null이면 모두의 이야기

    private Long board_id;
    private String boardName;

    private Long post_id;
    private Long comment_id;

    private Slice<ChatInfo> chatList;

    private Long my_id;
    private Long opponent_id;
    private String opponent_image;

    @Data
    @NoArgsConstructor
    public static class ChatInfo {
        private Long chat_id;
        private Long sender_id;
        private Long receiver_id;
        private LocalDate date;
        private LocalTime time;
        private String message;

        public ChatInfo(Chat c) {
            this.chat_id = c.getId();
            this.sender_id = c.getSender().getId();
            this.receiver_id = c.getReceiver().getId();
            this.date = c.getDate();
            this.time = c.getTime();
            this.message = c.getMessage();
        }
    }

    public ChatDTO(ChatRoom chatRoom, Slice<ChatInfo> chatList) {
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
        this.room_id = chatRoom.getId();
        if (chatRoom.getComment() != null) {
            this.comment_id = chatRoom.getComment().getId();
        }
        this.chatList = chatList;
        if (chatRoom.getReceiver() != null) {
            this.my_id = chatRoom.getReceiver().getId();
        }
        if (chatRoom.getSender() != null) {
            this.opponent_id = chatRoom.getSender().getId();
        }

    }

    public void updateImage(String imageUrl) {
        this.opponent_image = imageUrl;
    }

}
