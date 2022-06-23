package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private LocalDate date;             // 쪽지 발생 날짜
    private LocalTime time;             // 쪽지 발생 시간
    private String message;             // 쪽지 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;              // 수신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;                // 발신자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;


    public Chat(String message, User receiver, User sender, Post post) {
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.post = post;
    }

    public void updateComment(Comment comment) {
        this.comment = comment;
    }
}
