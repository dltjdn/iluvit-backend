package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupTeacherRequest {
    private String nickname;
    private String loginId;
    private String password;
    private String passwordCheck;
    private String phoneNum;
    private String emailAddress;
    private String name;
    private Auth auth;
    private Long centerId;

    public Teacher createTeacher(Center center, String pwd){
        return Teacher.builder()
                .nickName(nickname)
                .loginId(loginId)
                .password(pwd)
                .phoneNumber(phoneNum)
                .hasProfileImg(false)
                .emailAddress(emailAddress)
                .name(name)
                .auth(auth)
                .center(center)
                .approval(Approval.WAITING)
                .build();
    }

}
