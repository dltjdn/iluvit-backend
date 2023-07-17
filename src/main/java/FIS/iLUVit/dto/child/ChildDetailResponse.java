package FIS.iLUVit.dto.child;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.enumtype.Approval;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ChildDetailResponse {
    private Long childId;
    private String childName;
    private LocalDate birthDate;
    private String profileImage;
    private String centerName;
    private Approval approval;

    public ChildDetailResponse(Child child, String profileImage) {
        this.childId = child.getId();
        this.childName = child.getName();
        this.birthDate = child.getBirthDate();
        this.profileImage = profileImage;
        if (child.getCenter() != null) {
            this.centerName = child.getCenter().getName();
        }
        this.approval = child.getApproval();
    }
}
