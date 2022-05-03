package FIS.iLUVit.domain;

import javax.persistence.*;

@Entity
public class ReviewHeart {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
