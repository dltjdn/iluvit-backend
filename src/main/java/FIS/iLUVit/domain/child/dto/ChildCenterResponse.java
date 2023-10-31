package FIS.iLUVit.domain.child.dto;

import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.common.domain.Approval;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ChildCenterResponse {
    private Long id;
    private String name;
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("center_name")
    private String centerName;
    private String profileImage;
    private Approval approval;

    public static ChildCenterResponse from(Child child){
        ChildCenterResponseBuilder builder = ChildCenterResponse.builder()
                .id(child.getId())
                .name(child.getName())
                .centerId(child.getCenter().getId())
                .centerName(child.getCenter().getName())
                .profileImage(child.getProfileImagePath())
                .approval(child.getApproval());


        if(child.getCenter() != null) {
            builder.centerId(child.getCenter().getId())
                    .centerName(child.getCenter().getName());
        }

        return builder.build();
    }
}
