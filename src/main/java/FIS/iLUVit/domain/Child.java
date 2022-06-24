package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Approval;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Child extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String birthDate;
    private Boolean hasProfileImg;              // 프사 있나?
    @Enumerated(EnumType.STRING)
    private Approval approval;                  // 승인 여부 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @Builder
    public Child(String name, String birthDate, Boolean hasProfileImg, Approval approval, Parent parent, Center center) {
        this.name = name;
        this.birthDate = birthDate;
        this.hasProfileImg = hasProfileImg;
        this.approval = approval;
        this.parent = parent;
        this.center = center;
    }

    public static Child createChild(String name, String birthDate, Approval approval, Parent parent) {
        return Child.builder()
                .name(name)
                .birthDate(birthDate)
                .approval(approval)
                .parent(parent)
                .build();
    }

    public void mappingParent(Parent parent) {
        this.parent = parent;
        parent.getChildren().add(this);
    }

    public void mappingCenter(Center center) {
        this.center = center;
    }
}
