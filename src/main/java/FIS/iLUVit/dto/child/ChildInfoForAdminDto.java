package FIS.iLUVit.dto.child;

import FIS.iLUVit.domain.iluvit.Child;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChildInfoForAdminDto {

    private Long child_id;
    private String parent_name;
    private String parent_phoneNum;
    private String child_name;
    private String child_profileImg;
    private Approval approval;

    public ChildInfoForAdminDto(Child child,String child_profileImg) {
        this.child_id = child.getId();
        this.parent_name = child.getParent().getName();
        this.parent_phoneNum = child.getParent().getPhoneNumber();
        this.child_name = child.getName();
        this.approval = child.getApproval();
        this.child_profileImg = child_profileImg;
    }
}
