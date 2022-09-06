package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Approval;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Child extends BaseImageEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private Approval approval;                  // 승인 여부 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    @Builder
    public Child(Long id, String name, LocalDate birthDate, Approval approval, Parent parent, Center center) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.approval = approval;
        this.parent = parent;
        this.center = center;
    }

    public static Child createChild(String name, LocalDate birthDate, Approval approval, Parent parent) {
        return Child.builder()
                .name(name)
                .birthDate(birthDate)
                .approval(approval)
                .parent(parent)
                .build();
    }

    public void mappingCenter(Center center) {
        this.center = center;
        this.approval = Approval.WAITING;
    }

    public void accepted() {
        this.approval = Approval.ACCEPT;
    }

    public void update(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    public void exitCenter() {
        this.center = null;
        this.approval = Approval.REJECT;
    }
}
