package FIS.iLUVit.domain;
import javax.persistence.*;

@Entity
public class Bookmark {
    @Id @GeneratedValue
    private Long id;

    @Column(name = "orders")
    private Integer order;              //즐겨찾기 순서

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
