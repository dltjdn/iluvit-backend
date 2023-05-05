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
public class ReviewHeart extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ReviewHeart(Review review, User user) {
        this.review = review;
        this.user = user;
        review.getReviewHearts().add(this);
    }

    @Builder(toBuilder = true)
    public ReviewHeart(Long id, Review review, User user) {
        this.id = id;
        this.review = review;
        this.user = user;
    }
}
