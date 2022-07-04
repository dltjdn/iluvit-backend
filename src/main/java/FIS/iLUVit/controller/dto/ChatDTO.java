package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Chat;
import FIS.iLUVit.domain.ChatRoom;
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
        Board getBoard = chatRoom.getPost().getBoard();
        Center getCenter = getBoard.getCenter();
        this.room_id = chatRoom.getId();
        this.center_id = getCenter != null ? getCenter.getId() : null;
        this.centerName =  getCenter != null ? getCenter.getName() : "모두의 이야기";
        this.board_id = getBoard.getId();
        this.boardName = getBoard.getName();
        this.post_id = chatRoom.getPost().getId();
        if (chatRoom.getComment() != null) {
            this.comment_id = chatRoom.getComment().getId();
        }
        this.chatList = chatList;
    }


}
