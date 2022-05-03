package FIS.iLUVit.domain;

import javax.persistence.*;

@Entity
public class Waiting {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "ptDate_id")
    private PtDate ptDate;

}
