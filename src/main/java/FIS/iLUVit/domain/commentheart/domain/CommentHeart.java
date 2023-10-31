package FIS.iLUVit.domain.commentheart.domain;

import FIS.iLUVit.domain.comment.domain.Comment;
import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentHeart extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    @Builder(access = AccessLevel.PRIVATE)
    public CommentHeart(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }

    public static CommentHeart of(User user, Comment comment){
        return CommentHeart.builder()
                .user(user)
                .comment(comment)
                .build();
    }
}
