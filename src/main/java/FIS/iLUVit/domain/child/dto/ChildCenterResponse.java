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
public class ChildCenterResponse {
    private Long id;
    private String name;
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("center_name")
    private String centerName;
    private String profileImage;
    private Approval approval;

    public ChildCenterResponse(Child child, String image) {
        this.id = child.getId();
        this.name = child.getName();
        if (child.getCenter() != null) {
            this.centerId = child.getCenter().getId();
            this.centerName = child.getCenter().getName();
        }
        this.profileImage = image;
        this.approval = child.getApproval();
    }
}
