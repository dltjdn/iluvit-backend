package FIS.iLUVit.domain.child.dto;

import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.common.domain.Approval;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChildInfoForAdminResponse {

    private Long childId;
    private String parentName;
    private String parentPhoneNum;
    private String childName;
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
