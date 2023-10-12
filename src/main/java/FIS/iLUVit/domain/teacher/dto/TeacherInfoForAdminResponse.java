package FIS.iLUVit.domain.teacher.dto;

import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class TeacherInfoForAdminResponse {
    private Long teacherId;
    private String name;
    private String nickName;
    private Approval approval;
    private Auth auth;
    private String profileImg;

    public static TeacherInfoForAdminResponse from(Teacher teacher){
        return TeacherInfoForAdminResponse.builder()
                .teacherId(teacher.getId())
                .name(teacher.getName())
                .nickName(teacher.getNickName())
                .approval(teacher.getApproval())
                .auth(teacher.getAuth())
                .profileImg(teacher.getProfileImagePath())
                .build();
    }
}
