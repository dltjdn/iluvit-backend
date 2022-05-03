package FIS.iLUVit.domain;

import javax.persistence.*;

@Entity
public class ScrapPost {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "scrap_id")
    private Scrap scrap;
}
