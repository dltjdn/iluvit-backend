package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseEntity{
    @Id @GeneratedValue
    private Long id;
    private String name;                        // 게시판 이름
    @Enumerated(EnumType.STRING)
    private BoardKind boardKind;
    private Boolean isDefault;

    @OneToMany(mappedBy = "board")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private List<Bookmark> bookmarks = new ArrayList<>();;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")             // null 이면 모두의 게시판
    private Center center;

    public static Board createBoard(String name, BoardKind boardKind, Center center, Boolean isDefault) {
        Board board = new Board();
        board.name = name;
        board.boardKind = boardKind;
        board.center = center;
        board.isDefault = isDefault;
        return board;
    }
}
