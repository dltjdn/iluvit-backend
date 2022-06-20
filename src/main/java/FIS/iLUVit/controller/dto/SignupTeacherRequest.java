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
    private String loginId;
    private String password;
    private String passwordCheck;
    private String name;
    private String nickname;
    private String phoneNum;
    private String emailAddress;
    private String address;
    private String addressDetail;
    private Long centerId;

    public Teacher createTeacher(Center center, String pwd){
        return Teacher.builder()
                .loginId(loginId)
                .password(pwd)
                .nickName(nickname)
                .name(name)
                .phoneNumber(phoneNum)
                .emailAddress(emailAddress)
                .address(address)
                .addressDetail(addressDetail)
                .hasProfileImg(false)
                .center(center)
                .approval(Approval.WAITING)
                .auth(Auth.TEACHER)
                .build();
    }

}
