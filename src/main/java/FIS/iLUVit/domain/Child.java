package FIS.iLUVit.domain;

import FIS.iLUVit.controller.dto.UpdateChildRequest;
import FIS.iLUVit.domain.enumtype.Approval;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Child extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private LocalDate birthDate;
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
    public Child(String name, LocalDate birthDate, Boolean hasProfileImg, Approval approval, Parent parent, Center center) {
        this.name = name;
        this.birthDate = birthDate;
        this.hasProfileImg = hasProfileImg;
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

    public void mappingParent(Parent parent) {
        this.parent = parent;
        parent.getChildren().add(this);
    }

    public void mappingCenter(Center center) {
        this.center = center;
    }

    public void update(Center center, String name, LocalDate birthDate, MultipartFile image) {
        if (!center.getId().equals(this.getCenter().getId())) {
            this.center = center;
            this.approval = Approval.WAITING;
        }
        this.name = name;
        this.birthDate = birthDate;
        if (image != null) {
            this.hasProfileImg = true;
        }
    }
}
