package FIS.iLUVit.domain.child.dto;

import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.common.domain.Approval;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ChildFindForAdminResponse {

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

    public static ChildFindForAdminResponse from(Child child){
        return ChildFindForAdminResponse.builder()
                .childId(child.getId())
                .parentName(child.getParent().getName())
                .parentPhoneNum(child.getParent().getPhoneNumber())
                .childName(child.getName())
                .approval(child.getApproval())
                .childProfileImg(child.getProfileImagePath())
                .build();
    }
}
