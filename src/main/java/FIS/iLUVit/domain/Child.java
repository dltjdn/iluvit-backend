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

    public static Child createChild(String name, String birthDate, Approval approval) {
        Child child = new Child();
        child.name = name;
        child.birthDate = birthDate;
        child.approval = approval;
        return child;
    }

    public void mappingParent(Parent parent) {
        this.parent = parent;
        parent.getChildren().add(this);
    }

    public void mappingCenter(Center center) {
        this.center = center;
    }
}
