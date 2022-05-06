package FIS.iLUVit.domain;

import javax.persistence.*;

@Entity
public class Waiting extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ptDate_id")
    private PtDate ptDate;

}
