package FIS.iLUVit.domain.child.dto;

import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.common.domain.Approval;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChildFindOneResponse {
    @JsonProperty("child_id")
    private Long childId;
    @JsonProperty("child_name")
    private String childName;
    private LocalDate birthDate;
    private String profileImage;
    @JsonProperty("center_name")
    private String centerName;
    private Approval approval;

    public static ChildFindOneResponse from (Child child){
        ChildFindOneResponseBuilder builder = ChildFindOneResponse.builder()
                .childId(child.getId())
                .childName(child.getName())
                .birthDate(child.getBirthDate())
                .profileImage(child.getProfileImagePath())
                .approval(child.getApproval());

        if (child.getCenter() != null) {
            builder.centerName(child.getCenter().getName());
        }

        return builder.build();

    }
}
