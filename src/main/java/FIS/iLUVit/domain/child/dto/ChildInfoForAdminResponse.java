package FIS.iLUVit.domain.child.dto;

import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.common.domain.Approval;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChildInfoForAdminResponse {

    @JsonProperty("child_id")
    private Long childId;
    @JsonProperty("parent_name")
    private String parentName;

    @JsonProperty("parent_phoneNum")
    private String parentPhoneNum;

    @JsonProperty("child_name")
    private String childName;
    @JsonProperty("child_profileImg")
    private String childProfileImg;
    private Approval approval;

    public ChildInfoForAdminResponse(Child child, String childProfileImg) {
        this.childId = child.getId();
        this.parentName = child.getParent().getName();
        this.parentPhoneNum = child.getParent().getPhoneNumber();
        this.childName = child.getName();
        this.approval = child.getApproval();
        this.childProfileImg = childProfileImg;
    }
}
