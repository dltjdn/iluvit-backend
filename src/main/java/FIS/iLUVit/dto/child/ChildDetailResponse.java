package FIS.iLUVit.dto.child;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.enumtype.Approval;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChildDetailResponse {
    private Long child_id;
    private String child_name;
    private LocalDate birthDate;
    private String profileImage;
    private String center_name;
    private Approval approval;

    public ChildDetailResponse(Child child) {
        this.child_id = child.getId();
        this.child_name = child.getName();
        this.birthDate = child.getBirthDate();
        if (child.getCenter() != null) {
            this.center_name = child.getCenter().getName();
        }
        this.approval = child.getApproval();
    }
}
