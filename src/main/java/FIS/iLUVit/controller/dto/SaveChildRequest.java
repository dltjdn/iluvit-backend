package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.Approval;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveChildRequest {
    private Long center_id;
    private String name;
    private String birthDate;
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
