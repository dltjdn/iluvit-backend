package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.enumtype.Approval;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;

@Data
public class ChildInfoDetailResponse {
    private Long child_id;
    private String child_name;
    private LocalDate birthDate;
    private String profileImage;
    private String sido;
    private String sigungu;
    private String center_name;
    private Approval approval;
    Slice<CenterInfoDto> centerInfoDtoSlice;

    public ChildInfoDetailResponse(Child child) {
        this.child_id = child.getId();
        this.child_name = child.getName();
        this.birthDate = child.getBirthDate();
        this.sido = child.getCenter().getArea().getSido();
        this.sigungu = child.getCenter().getArea().getSigungu();
        this.center_name = child.getCenter().getName();
        this.approval = child.getApproval();
    }
}
