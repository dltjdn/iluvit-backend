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
public class ChildInfoDto {

    private List<ChildInfo> data = new ArrayList<>();

    @Data
    public static class ChildInfo{
        private Long id;
        private String name;
        private Long center_id;
        private String center_name;
        private String profileImage;
        private Approval approval;

        public ChildInfo(Child child, String image) {
            this.id = child.getId();
            this.name = child.getName();
            if (child.getCenter() != null) {
                this.center_id = child.getCenter().getId();
                this.center_name = child.getCenter().getName();
            }
            this.profileImage = image;
            this.approval = child.getApproval();
        }
    }
}
