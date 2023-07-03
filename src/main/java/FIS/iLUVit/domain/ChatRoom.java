package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private LocalDate date;             // 쪽지 발생 날짜
    private LocalTime time;             // 쪽지 발생 시간
    private String message;             // 쪽지 내용

    private Long partner_id;            // 상대 ChatRoom 아이디

    private Boolean anonymous;          // 프로필 익명 여부 -> true 설정하면 양쪽 다 익명으로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public ChatRoom(User receiver, User sender, Post post, Boolean anonymous) {
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.receiver = receiver;
        this.sender = sender;
        this.post = post;
        this.anonymous = anonymous;
    }

    public void updateComment(Comment comment) {
        this.comment = comment;
    }

    public void updateMessage(String message) {
        this.message = message;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    public void updatePartnerId(Long partner_id) {
        this.partner_id = partner_id;
    }
}
