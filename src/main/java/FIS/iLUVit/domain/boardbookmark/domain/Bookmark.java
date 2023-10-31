package FIS.iLUVit.domain.boardbookmark.domain;
import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.user.domain.User;
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

    @Builder(access = AccessLevel.PRIVATE)
    public Bookmark(Board board, User user) {
        this.board = board;
        this.user = user;
    }

    public static Bookmark of(Board board, User user) {
        return Bookmark.builder()
                .board(board)
                .user(user)
                .build();
    }

}
