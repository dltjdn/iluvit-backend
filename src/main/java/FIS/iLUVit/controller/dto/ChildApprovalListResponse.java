package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.enumtype.Approval;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildApprovalListResponse {

    private List<ChildInfoForAdmin> data = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class ChildInfoForAdmin{
        private Long child_id;
        private String parent_name;
        private String parent_phoneNum;
        private String child_name;
        private String child_profileImg;
        private Approval approval;

        public ChildInfoForAdmin(Child child) {
            this.child_id = child.getId();
            this.parent_name = child.getParent().getName();
            this.parent_phoneNum = child.getParent().getPhoneNumber();
            this.child_name = child.getName();
            this.approval = child.getApproval();
        }
    }
}
