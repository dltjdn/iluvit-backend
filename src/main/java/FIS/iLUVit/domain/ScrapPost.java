package FIS.iLUVit.domain;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class ScrapPost extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id")
    private Scrap scrap;

    public static ScrapPost createScrapPost(Post post, Scrap scrap) {
        ScrapPost scrapPost = new ScrapPost();
        scrapPost.post = post;
        post.getScrapPosts().add(scrapPost);
        scrapPost.scrap = scrap;
        scrap.getScrapPosts().add(scrapPost);
        return scrapPost;
    }
}
