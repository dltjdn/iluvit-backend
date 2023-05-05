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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"board_id", "user_id"}
                )
        }
)
public class Bookmark extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Bookmark(Board board, User user) {
        this.board = board;
        this.user = user;
    }

    public static Bookmark createBookmark(Board board, User user) {
        Bookmark bookmark = new Bookmark();
        bookmark.board = board;
        bookmark.user = user;
        return bookmark;
    }

    @Builder(toBuilder = true)
    public Bookmark(Long id, Board board, User user) {
        this.id = id;
        this.board = board;
        this.user = user;
    }
}
