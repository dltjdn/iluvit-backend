package FIS.iLUVit.domain;
import javax.persistence.*;

@Entity
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
}
