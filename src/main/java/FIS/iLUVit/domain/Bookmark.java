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

    @Column(name = "orders")
    private Integer order;              //즐겨찾기 순서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Bookmark(Integer order, Board board, User user) {
        this.order = order;
        this.board = board;
        this.user = user;
    }

    public static Bookmark createBookmark(Integer order, Board board, User user) {
        Bookmark bookmark = new Bookmark();
        bookmark.order = order;
        bookmark.board = board;
        bookmark.user = user;
        return bookmark;
    }
}
