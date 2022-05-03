package FIS.iLUVit.domain;

import javax.persistence.*;

@Entity
public class AddInfo {
    @Id
    @GeneratedValue
    private Long id;

    private String info;                // 부가정보

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;
}
