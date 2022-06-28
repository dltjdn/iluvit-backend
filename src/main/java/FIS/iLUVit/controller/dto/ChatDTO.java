package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Chat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ChatDTO {
    private Long chat_id;
    private Long sender_id;
    private Long receiver_id;
    private LocalDate date;
    private LocalTime time;
    private String message;

    private Long center_id;
    private String centerName; // center_id null이면 모두의 이야기

    private Long board_id;
    private String boardName;

    private Long post_id;
    private Long comment_id;

    public ChatDTO(Chat c) {
        this.chat_id = c.getId();
        this.sender_id = c.getSender().getId();
        this.receiver_id = c.getReceiver().getId();
        this.date = c.getDate();
        this.time = c.getTime();
        this.message = c.getMessage();
        Center getCenter = c.getPost().getBoard().getCenter();
        if (getCenter != null) {
            this.center_id = c.getPost().getBoard().getCenter().getId();
            this.centerName = c.getPost().getBoard().getCenter().getName() + "의 이야기";
        } else {
            this.centerName = "모두의 이야기";
        }
        this.board_id = c.getPost().getBoard().getId();
        this.boardName = c.getPost().getBoard().getName();
        this.post_id = c.getPost().getId();
        if (c.getComment() != null) {
            this.comment_id = c.getComment().getId();
        }
    }
}
