package FIS.iLUVit.domain;

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
    public ScrapPost(Long id, Post post, Scrap scrap) {
        this.id = id;
        this.post = post;
        this.scrap = scrap;
    }

    public static ScrapPost createScrapPost(Post post, Scrap scrap) {
        ScrapPost scrapPost = new ScrapPost();
        scrapPost.post = post;
        scrapPost.scrap = scrap;
        return scrapPost;
    }
}
