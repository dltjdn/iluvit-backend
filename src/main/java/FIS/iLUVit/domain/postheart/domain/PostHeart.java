package FIS.iLUVit.domain.postheart.domain;

import FIS.iLUVit.domain.post.domain.Post;
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

    @Builder(access = AccessLevel.PRIVATE)
    public PostHeart(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public static PostHeart of(User user, Post post){
        return PostHeart.builder()
                .user(user)
                .post(post)
                .build();
    }
}
