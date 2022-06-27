package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Chat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ChatListDTO {

    private Long chat_id;

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

    public ChatListDTO(Chat c) {
        this.chat_id = c.getId();
        Center getCenter = c.getPost().getBoard().getCenter();
        if (getCenter != null) {
            this.center_id = c.getPost().getBoard().getCenter().getId();
            this.centerName = c.getPost().getBoard().getCenter().getName() + "의 이야기";
        } else {
            this.centerName = "모두의 이야기";
        }
        this.board_id = c.getPost().getBoard().getId();
        this.boardName = c.getPost().getBoard().getName();
        this.recentMessage = c.getMessage();
        this.date = c.getDate();
        this.time = c.getTime();
        this.receiver_id = c.getReceiver().getId();
        this.post_id = c.getPost().getId();
        if (c.getComment() != null) {
            this.comment_id = c.getComment().getId();
        }
    }
}
