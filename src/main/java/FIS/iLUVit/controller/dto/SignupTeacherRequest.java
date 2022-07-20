package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupTeacherRequest {
    @Size(min=5, message = "아이디는 5자 이상이어야합니다.")
    private String loginId;
    @Size(min=8, message = "비밀번호는 8자 이상이어야합니다.")
    private String password;
    @NotNull
    private String passwordCheck;
    @NotNull
    private String name;
    @NotNull
    private String nickname;
    @NotNull
    private String phoneNum;
    @NotNull
    private String emailAddress;
    @NotNull
    private String address;
    @NotNull
    private String detailAddress;
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
                .detailAddress(detailAddress)
                .hasProfileImg(false)
                .center(center)
                .approval(Approval.WAITING)
                .auth(Auth.TEACHER)
                .build();
    }

}
