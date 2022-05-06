package FIS.iLUVit.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Alarm extends BaseEntity{
    @Id @GeneratedValue
    private Long id;
    private LocalDate date;             // 알림 발생 날짜
    private LocalTime time;             // 알림 발생 시간


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
