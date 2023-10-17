package FIS.iLUVit.domain.scrap.domain;

import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.common.domain.BaseEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class ScrapPost extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Scrap scrap;

    @Builder
    public ScrapPost(Post post, Scrap scrap) {
        this.post = post;
        this.scrap = scrap;
    }

    public static ScrapPost of(Post post, Scrap scrap) {
        return ScrapPost.builder()
                .post(post)
                .scrap(scrap)
                .build();
    }
}