package FIS.iLUVit.domain.comment.domain;

import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Integer anonymousOrder;  // 댓글 작성 순서 ex) 익명1, 익명2, .... // anonymous false 일 때는 null
    private Integer heartCnt;        // 좋아요 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> subComments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pComment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parentComment;

    public Comment(Boolean anonymous, String content, Post post, User user, Integer anonymousOrder) {
        this.anonymous = anonymous;
        this.content = content;
        this.post = post;
        post.updateComment(this);
        this.user = user;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.anonymousOrder = anonymousOrder;
        this.heartCnt = 0;
    }

    public void updateParentComment(Comment parentComment) {
        this.parentComment = parentComment;
        parentComment.getSubComments().add(this);
    }

    @Builder(toBuilder = true)
    public Comment(Long id, LocalDate date, LocalTime time, Boolean anonymous, String content, Integer anonymousOrder, Integer heartCnt, Post post, User user, List<Comment> subComments, Comment parentComment) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.anonymous = anonymous;
        this.content = content;
        this.anonymousOrder = anonymousOrder;
        this.heartCnt = heartCnt;
        this.post = post;
        this.user = user;
        this.subComments = subComments;
        this.parentComment = parentComment;
    }



    public void deleteComment() {
        this.content = "삭제된 댓글입니다.";
        this.user = null;
        this.getPost().reduceCommentCnt();
    }

    public void plusHeartCnt() {
        this.heartCnt += 1;
    }

    public void minusHeartCnt() {
        this.heartCnt -= 1;
    }
}
