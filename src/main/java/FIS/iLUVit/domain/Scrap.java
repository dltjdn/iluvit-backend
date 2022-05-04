package FIS.iLUVit.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Scrap {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "scrap")
    private List<ScrapPost> scrapPosts;
}
