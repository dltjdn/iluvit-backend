package FIS.iLUVit.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Table(name = "Comments")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDate date;          // 게시글 작성 날짜
    private LocalTime time;          // 게시글 작성 시간
    private Boolean anonymous;       // 익명 댓글 여부
    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "comment")
    private List<CommentHeart> commentHearts;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> subComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pComment_id")
    private Comment parentComment;



}
