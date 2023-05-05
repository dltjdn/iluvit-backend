package FIS.iLUVit.domain.iluvit;

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
public class PostHeart extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    public PostHeart(User user, Post post) {
        this.user = user;
        this.post = post;
        post.updatePostHeart(this);
    }

    @Builder(toBuilder = true)
    public PostHeart(Long id, User user, Post post) {
        this.id = id;
        this.user = user;
        this.post = post;
    }
}
