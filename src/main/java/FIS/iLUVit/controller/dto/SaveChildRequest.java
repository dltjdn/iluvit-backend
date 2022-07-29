package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.enumtype.Approval;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveChildRequest {
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
                .hasProfileImg(!this.profileImg.isEmpty())
                .build();
    }
}
