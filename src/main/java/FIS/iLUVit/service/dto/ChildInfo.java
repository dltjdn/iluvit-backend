package FIS.iLUVit.service.dto;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildInfo {
    private Long id;
    private String name;
    private Boolean hasProfileImg;
    private Approval approval;

    public ChildInfo(Child child) {
        this.id = child.getId();
        this.name = child.getName();
        this.hasProfileImg = child.getHasProfileImg();
        this.approval = child.getApproval();
    }
}
