package FIS.iLUVit.domain.board.domain;

import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.center.domain.Center;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;                        // 게시판 이름
    @Enumerated(EnumType.STRING)
    private BoardKind boardKind;
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")             // null 이면 모두의 게시판
    private Center center;

    @Builder(access = AccessLevel.PRIVATE)
    public Board(String name, BoardKind boardKind, Center center, Boolean isDefault){
        this.name = name;
        this.boardKind = boardKind;
        this.center = center;
        this.isDefault = isDefault;
    }

    public static Board publicOf(String name, BoardKind boardKind, Boolean isDefault) {
        return Board.builder()
                .name(name)
                .boardKind(boardKind)
                .isDefault(isDefault)
                .build();
    }

    public static Board centerOf(String name, BoardKind boardKind, Center center, Boolean isDefault) {
        return Board.builder()
                .name(name)
                .boardKind(boardKind)
                .center(center)
                .isDefault(isDefault)
                .build();
    }
}
