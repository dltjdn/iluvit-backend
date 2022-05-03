package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Approval;

import javax.persistence.*;

@Entity
public class Child {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String birthDate;
    @Enumerated(EnumType.STRING)
    private Approval approval;                  // 승인 여부 상태

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;
}
