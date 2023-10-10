package FIS.iLUVit.domain.child.dto;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.common.domain.Approval;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChildCreateRequest {
    private Long centerId;
    @NotBlank(message = "이름을 입력해주세요")
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private MultipartFile profileImg;


    public Child createChild(Center center, Parent parent) {
        return Child.builder()
                .name(name)
                .birthDate(birthDate)
                .approval(Approval.WAITING)
                .center(center)
                .parent(parent)
                .build();
    }
}
