package FIS.iLUVit.domain.chat.domain;

import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.user.domain.User;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private LocalDate date;             // 쪽지 발생 날짜
    private LocalTime time;             // 쪽지 발생 시간
    private String message;             // 쪽지 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;              // 수신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;                // 발신자

    @Builder(access = AccessLevel.PRIVATE)
    public Chat(LocalDate date, LocalTime time, String message, User receiver, ChatRoom chatRoom, User sender) {
        this.date = date;
        this.time = time;
        this.message = message;
        this.chatRoom = chatRoom;
        this.receiver = receiver;
        this.sender = sender;
    }

    public static Chat of(String message, ChatRoom chatRoom, User receiver, User sender){
        return Chat.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .message(message)
                .chatRoom(chatRoom)
                .receiver(receiver)
                .sender(sender)
                .build();
    }

    public void updateChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        chatRoom.updateMessage(this.message);
    }

}
