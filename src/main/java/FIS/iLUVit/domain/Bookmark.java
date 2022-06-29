package FIS.iLUVit.domain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
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
}
