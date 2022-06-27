package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.enumtype.Approval;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChildInfoDTO {

    private List<ChildInfo> data = new ArrayList<>();

    @Data
    public static class ChildInfo{
        private Long id;
        private String name;
        private Long center_id;
        private String center_name;
        private Boolean hasProfileImg;
        private Approval approval;

        public ChildInfo(Child child) {
            this.id = child.getId();
            this.name = child.getName();
            this.center_id = child.getCenter().getId();
            this.center_name = child.getCenter().getName();
            this.hasProfileImg = child.getHasProfileImg();
            this.approval = child.getApproval();
        }
    }
}
