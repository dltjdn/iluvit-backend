package FIS.iLUVit.domain;

import javax.persistence.*;

@Entity
public class Program {
    @Id @GeneratedValue
    private Long id;

    private String name;                // 프로그램 명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;
}
