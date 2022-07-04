package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.ChatRoom;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ChatListDTO {

    private Long room_id;

    private Long center_id;
    private String centerName; // center_id null이면 모두의 이야기

    private Long board_id;
    private String boardName;

    private String recentMessage;
    private LocalDate date;
    private LocalTime time;

    private Long receiver_id;

    private Long post_id;
    private Long comment_id;

    public ChatListDTO(ChatRoom chatRoom) {
        this.room_id = chatRoom.getId();
        Center getCenter = chatRoom.getPost().getBoard().getCenter();
        if (getCenter != null) {
            this.center_id = chatRoom.getPost().getBoard().getCenter().getId();
            this.centerName = chatRoom.getPost().getBoard().getCenter().getName() + "의 이야기";
        } else {
            this.centerName = "모두의 이야기";
        }
        this.board_id = chatRoom.getPost().getBoard().getId();
        this.boardName = chatRoom.getPost().getBoard().getName();
        this.recentMessage = chatRoom.getMessage();
        this.date = chatRoom.getDate();
        this.time = chatRoom.getTime();
        this.receiver_id = chatRoom.getReceiver().getId();
        this.post_id = chatRoom.getPost().getId();
        if (chatRoom.getComment() != null) {
            this.comment_id = chatRoom.getComment().getId();
        }
    }
}
