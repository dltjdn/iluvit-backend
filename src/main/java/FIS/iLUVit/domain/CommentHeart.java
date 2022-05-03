package FIS.iLUVit.domain;

import javax.persistence.*;

@Entity
public class CommentHeart {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
