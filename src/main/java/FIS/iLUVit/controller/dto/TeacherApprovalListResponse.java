package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherApprovalListResponse {

    private List<TeacherInfoForAdmin> data = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class TeacherInfoForAdmin{
        private Long teacher_id;
        private String name;
        private Approval approval;
        private Auth auth;
        private String profileImg;

        public TeacherInfoForAdmin(Long id, String name, Approval approval, Auth auth) {
            this.teacher_id = id;
            this.name = name;
            this.approval = approval;
            this.auth = auth;
        }
    }
}
