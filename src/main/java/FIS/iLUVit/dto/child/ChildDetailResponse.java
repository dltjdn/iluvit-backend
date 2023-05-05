package FIS.iLUVit.dto.child;

import FIS.iLUVit.domain.iluvit.Child;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ChildDetailResponse {
    private Long child_id;
    private String child_name;
    private LocalDate birthDate;
    private String profileImage;
    private String center_name;
    private Approval approval;

    public ChildDetailResponse(Child child, String profileImage) {
        this.child_id = child.getId();
        this.child_name = child.getName();
        this.birthDate = child.getBirthDate();
        this.profileImage = profileImage;
        if (child.getCenter() != null) {
            this.center_name = child.getCenter().getName();
        }
        this.approval = child.getApproval();
    }
}
