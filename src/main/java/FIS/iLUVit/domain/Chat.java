package FIS.iLUVit.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Chat {
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
}
