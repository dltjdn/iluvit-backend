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

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveChildRequest {
    private Long center_id;
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
