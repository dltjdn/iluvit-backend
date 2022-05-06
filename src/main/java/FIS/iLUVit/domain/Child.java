package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Approval;

import javax.persistence.*;

@Entity
public class Child extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String birthDate;
    @Enumerated(EnumType.STRING)
    private Approval approval;                  // 승인 여부 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;
}
