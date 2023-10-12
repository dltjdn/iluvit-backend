package FIS.iLUVit.domain.child.dto;

import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.common.domain.Approval;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ChildDetailResponse {
    @JsonProperty("child_id")
    private Long childId;
    @JsonProperty("child_name")
    private String childName;
    private LocalDate birthDate;
    private String profileImage;
    @JsonProperty("center_name")
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
