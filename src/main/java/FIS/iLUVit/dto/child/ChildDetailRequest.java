package FIS.iLUVit.dto.child;

import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.Child;
import FIS.iLUVit.domain.iluvit.Parent;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
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
public class ChildDetailRequest {
    private Long center_id;
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
