package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "comment")
    private List<CommentHeart> commentHearts = new ArrayList<>();

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> subComments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pComment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parentComment;

    public Comment(Boolean anonymous, String content, Post post, User user) {
        this.anonymous = anonymous;
        this.content = content;
        this.post = post;
        post.updateComment(this);
        this.user = user;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    public void updateParentComment(Comment parentComment) {
        this.parentComment = parentComment;
        parentComment.getSubComments().add(this);
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateUser(User user) {
        this.user = user;
    }
}
