package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.BoardKind;

import javax.persistence.*;
import java.util.List;

@Entity
public class Board extends BaseEntity{
    @Id @GeneratedValue
    private Long id;
    private String name;                        // 게시판 이름
    @Enumerated(EnumType.STRING)
    private BoardKind boardKind;

    @OneToMany(mappedBy = "board")
    private List<Post> posts;

    @OneToMany(mappedBy = "board")
    private List<Bookmark> bookmarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")             // null 이면 모두의 게시판
    private Center center;
}
