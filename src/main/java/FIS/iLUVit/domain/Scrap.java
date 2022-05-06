package FIS.iLUVit.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Scrap extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "scrap")
    private List<ScrapPost> scrapPosts;
}
